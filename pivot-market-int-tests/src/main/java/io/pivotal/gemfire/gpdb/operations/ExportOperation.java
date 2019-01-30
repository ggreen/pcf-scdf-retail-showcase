/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package io.pivotal.gemfire.gpdb.operations;

import io.pivotal.gemfire.gpdb.config.Field;
import io.pivotal.gemfire.gpdb.config.Type;
import io.pivotal.gemfire.gpdb.functions.CreateRowProducerResourceFunction;
import io.pivotal.gemfire.gpdb.functions.RemoveRowProducerResourceFunction;
import io.pivotal.gemfire.gpdb.functions.RowProducerResourceRowCountFunction;
import io.pivotal.gemfire.gpdb.monitor.GGCMonitor;
import io.pivotal.gemfire.gpdb.monitor.GGCStatisticsCompositeKey;
import io.pivotal.gemfire.gpdb.operationevents.TypeOperationEvent;
import io.pivotal.gemfire.gpdb.operations.OperationBase;
import io.pivotal.gemfire.gpdb.operations.OperationException;
import io.pivotal.gemfire.gpdb.util.AutoCloseableReference;
import io.pivotal.gemfire.gpdb.util.OperationUtil;
import io.pivotal.gemfire.gpdb.util.RowResourceArguments;
import io.pivotal.gemfire.gpdb.util.UserInterruptedException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExportOperation extends OperationBase {
	private static final Logger log = LogManager.getLogger();
	public static final String OPERATION_NAME = "export";

	public ExportOperation() {
		super(log, "export");
	}

	public void doExecute() throws Exception {
		GGCMonitor monitor = GGCMonitor.getInstance();
		String serverName = GemFireCacheImpl.getInstance().getDistributedSystem().getDistributedMember().getName();
		GGCStatisticsCompositeKey compositeKey = new GGCStatisticsCompositeKey(this.operationId, serverName,
				Thread.currentThread().getId());
		monitor.updateStatistics(compositeKey, 0L, 0L);
		Iterator arg3 = this.store.getTypes().getTypes().iterator();

		while (arg3.hasNext()) {
			Type type = (Type) arg3.next();
			List idFields = type.getId().getResolvedFields();
			List fields = type.getFields().getFields();
			if (this.getStore().getRegion().getAttributes().getDataPolicy().isReplicate()) {
				throw new OperationException("Export: This operation is not permitted on REPLICATE regions. Region : "
						+ this.getStore().getRegion().getName());
			}

			if (OperationUtil.listEqualsNoOrder(idFields, fields)) {
				throw new OperationException(
						"Export : Fields tag <gpdb:fields> in cache.xml should also contain at least one data field to perform Export.");
			}

			TypeOperationEvent event = new TypeOperationEvent(this.operationId, this.getStore().getRegion(), this.txId,
					type);
			this.fireBeforeType(event);
			OperationBase.Resources resources = this.createResources(this.txId, type);
			Throwable arg9 = null;

			try {
				AutoCloseableReference results = this.createTempTable(this.txId, this.conn, type);
				Throwable exportCount = null;

				int copyCount;
				try {
					AutoCloseableReference extTable = this.createExternalTable(this.conn, resources,
							(String) results.get());
					Throwable arg14 = null;

					try {
						String tmpTableName = (String) results.get();
						String extTableName = (String) extTable.get();
						String dstSchema = type.getSchema();
						String dstTableName = type.getTable();
						
						copyCount = copyTable(this.conn, null, extTableName, dstSchema, dstTableName, idFields, fields, false);
						/*
						copyCount = copyTable(this.conn, (String) null, extTableName, (String) null, tmpTableName,
								idFields, fields, false);
						
						int upsertCount = upsertTable(this.conn, (String) null, tmpTableName, dstSchema,
								dstTableName, idFields, fields);
						String errorMessage = null;
						if (copyCount < upsertCount) {
							errorMessage = String.format(
									"Export failed: Updated/received more rows in table \'%s\' than entries exported from region \'%s\'. This can happen if `<gpdb:id>` in \'cache.xml\' does not represent unique GPDB rows.",
									new Object[] { dstTableName, this.getStore().getRegion().getName() });
						} else if (copyCount > upsertCount) {
							errorMessage = String.format(
									"Export failed: # of rows updated/inserted in table \'%s\' is less than # of entries in region \'%s\'.",
									new Object[] { dstTableName, this.getStore().getRegion().getName() });
						}
						
						if (errorMessage != null) {
							log.error(errorMessage);
							log.debug(
									"txId: {} execute (elapsed time: {}): upsert mismatch copyCount: {} <> upsertCount: {}",
									this.txId, Long.valueOf(this.stopWatch.elapsedTimeMillis()),
									Integer.valueOf(copyCount), Integer.valueOf(upsertCount));
							throw new OperationException(errorMessage);
						}
						*/
					} catch (Throwable arg63) {
						arg14 = arg63;
						throw arg63;
					} finally {
						if (extTable != null) {
							if (arg14 != null) {
								try {
									extTable.close();
								} catch (Throwable arg62) {
									arg14.addSuppressed(arg62);
								}
							} else {
								extTable.close();
							}
						}

					}
				} catch (Throwable arg65) {
					exportCount = arg65;
					throw arg65;
				} finally {
					if (results != null) {
						if (exportCount != null) {
							try {
								results.close();
							} catch (Throwable arg61) {
								exportCount.addSuppressed(arg61);
							}
						} else {
							results.close();
						}
					}

				}

				ResultCollector results1 = FunctionService.onRegion(this.store.getRegion())
						.withArgs(RowProducerResourceRowCountFunction.arguments(this.txId))
						.execute(RowProducerResourceRowCountFunction.ID);
				long exportCount1 = RowProducerResourceRowCountFunction.getResult(results1);
				if ((long) copyCount != exportCount1) {
					log.printf(Level.ERROR, "%s: Expected %,d actual %,d.",
							new Object[] { this.txId, Integer.valueOf(copyCount), Long.valueOf(exportCount1) });
					throw new OperationException(String.format("Expected %,d actual %,d.",
							new Object[] { Integer.valueOf(copyCount), Long.valueOf(exportCount1) }));
				}

				this.rowsTransferred += exportCount1;
				this.fireAfterType(event);
			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				if (resources != null) {
					if (arg9 != null) {
						try {
							resources.close();
						} catch (Throwable e) {
							arg9.addSuppressed(e);
						}
					} else {
						resources.close();
					}
				}

			}
		}

		if (Thread.interrupted()) {
			throw new UserInterruptedException("User canceled the export operation.");
		}
	}

	private int upsertTable(Connection conn, String sourceSchema, String sourceTable, String destinationSchema,
			String destinationTable, List<Field> idFields, List<Field> fields) throws SQLException {
		int updateCount = this.doUpdate(conn, sourceSchema, sourceTable, destinationSchema, destinationTable, idFields,
				fields);
		int insertCount = this.copyTable(conn, sourceSchema, sourceTable, destinationSchema, destinationTable, idFields,
				fields, true);
		return updateCount + insertCount;
	}

	public Object getResult() {
		return Long.valueOf(this.rowsTransferred);
	}

	protected OperationBase.Resources createResources(final String id, Type<?> type) throws OperationException {
		ResultCollector results = FunctionService.onRegion(this.store.getRegion())
				.withArgs(new RowResourceArguments(id, type.getName(), this.getRowEventListeners(), this.operationId))
				.execute(CreateRowProducerResourceFunction.ID);

		try {
			OperationBase.Resources e = new OperationBase.Resources(id,
					(Collection) results.getResult(1L, TimeUnit.MINUTES)) {
				public void close() {
					try {
						FunctionService.onRegion(ExportOperation.this.store.getRegion())
								.withArgs(RemoveRowProducerResourceFunction.arguments(id))
								.execute(RemoveRowProducerResourceFunction.ID);
					} catch (Exception arg1) {
						ExportOperation.log.error(arg1);
					}

				}
			};
			if (log.isDebugEnabled()) {
				log.debug("{} {} createResources: Created resources {}.", this.txId,
						Long.valueOf(this.stopWatch.elapsedTimeMillis()), e);
			}

			return e;
		} catch (InterruptedException e) {
			throw new OperationException(e);
		}
	}
}
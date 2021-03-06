package com.cybex.componentservice.db.dao.impl;


import com.cybex.componentservice.db.GemmaDatabase;
import com.cybex.componentservice.db.dao.MultiWalletEntityDao;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.FPEntity;
import com.cybex.componentservice.db.entity.FPEntity_Table;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity_Table;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.db.util.DBFlowUtil;
import com.cybex.componentservice.db.util.OperationType;
import com.cybex.componentservice.manager.LoggerManager;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.List;

public class MultiWalletEntityDaoImpl implements MultiWalletEntityDao {

    @Override
    public void deleteEntity(MultiWalletEntity entity) {
        if (EmptyUtils.isEmpty(entity)) { return; }

        entity.delete();
    }

    @Override
    public void deleteEntityAsync(MultiWalletEntity entity, DBCallback callback) {
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<EthWalletEntity> ethWalletEntities = entity.getEthWalletEntities();
                List<EosWalletEntity> eosWalletEntities = entity.getEosWalletEntities();
                List<FPEntity> fpEntities = entity.getFpEntities();
                if (ethWalletEntities != null) {
                    for (EthWalletEntity ethWalletEntity : ethWalletEntities) {
                        ethWalletEntity.delete();
                    }
                }
                if (eosWalletEntities != null) {
                    for (EosWalletEntity eosWalletEntity : eosWalletEntities) {
                        eosWalletEntity.delete();
                    }
                }
                if (fpEntities != null) {
                    for (FPEntity fpEntity : fpEntities) {
                        fpEntity.delete();
                    }
                }
                entity.delete();
            }
        }, callback);
    }

    @Override
    public MultiWalletEntity getCurrentMultiWalletEntity() {
        MultiWalletEntity entity = SQLite.select().from(MultiWalletEntity.class)
                .where(MultiWalletEntity_Table.isCurrentWallet.eq(1))
                .querySingle();
        return entity;
    }

    @Override
    public MultiWalletEntity getMultiWalletEntity(String walletName) {
        MultiWalletEntity entity = SQLite.select().from(MultiWalletEntity.class)
                .where(MultiWalletEntity_Table.walletName.eq(walletName))
                .querySingle();
        return entity;
    }

    @Override
    public MultiWalletEntity getMultiWalletEntityByID(int id) {
        MultiWalletEntity entity = SQLite.select().from(MultiWalletEntity.class)
                .where(MultiWalletEntity_Table.id.eq(id))
                .querySingle();
        return entity;
    }

    @Override
    public List<MultiWalletEntity> getMultiWalletEntityListByWalletType(int walletType) {
        List<MultiWalletEntity> list = SQLite.select().from(MultiWalletEntity.class)
                .where(MultiWalletEntity_Table.walletType.eq(walletType))
                .queryList();
        return list;
    }

    @Override
    public List<MultiWalletEntity> getMultiWalletEntityList() {
        List<MultiWalletEntity> list =
                SQLite.select().from(MultiWalletEntity.class)
                        .queryList();

        return list;
    }

    @Override
    public List<MultiWalletEntity> getBluetoothWalletList() {
        List<MultiWalletEntity> list =
                SQLite.select().from(MultiWalletEntity.class)
                        .where(MultiWalletEntity_Table.walletType.eq(MultiWalletEntity.WALLET_TYPE_HARDWARE))
                        .queryList();

        return list;
    }

    @Override
    public void saveOrUpateEntity(final MultiWalletEntity entity, DBCallback callback) {
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<EthWalletEntity> ethWalletEntities = entity.getEthWalletEntities();
                List<EosWalletEntity> eosWalletEntities = entity.getEosWalletEntities();
                List<FPEntity> fpEntities = entity.getFpEntities();
                if (ethWalletEntities != null) {
                    for (EthWalletEntity ethWalletEntity : ethWalletEntities) {
                        ethWalletEntity.save();
                    }
                }
                if (eosWalletEntities != null) {
                    for (EosWalletEntity eosWalletEntity : eosWalletEntities) {
                        eosWalletEntity.save();
                    }
                }
                if (fpEntities != null) {
                    for (FPEntity fpEntity : fpEntities) {
                        fpEntity.save();
                    }
                }
                entity.save();
            }
        }, callback);

    }

    @Override
    public void saveOrUpateEntitySync(MultiWalletEntity entity) {
        List<EthWalletEntity> ethWalletEntities = entity.getEthWalletEntities();
        List<EosWalletEntity> eosWalletEntities = entity.getEosWalletEntities();
        if (ethWalletEntities != null) {
            for (EthWalletEntity ethWalletEntity : ethWalletEntities) {
                ethWalletEntity.save();
            }
        }
        if (eosWalletEntities != null) {
            for (EosWalletEntity eosWalletEntity : eosWalletEntities) {
                eosWalletEntity.save();
            }
        }
        entity.save();
    }

    @Override
    public void batchSaveEntityListSync(List<MultiWalletEntity> list) {
        ITransaction transaction = DBFlowUtil.getTransaction(list, OperationType.TYPE_SAVE);
        DBFlowUtil.execTransactionSync(GemmaDatabase.class, transaction);
    }

    @Override
    public void batchSaveEntityListASync(List<MultiWalletEntity> list, DBCallback callback) {
        if (list == null || list.size() == 0) {
            if (null != callback) {
                callback.onFailed(new Throwable("object is null"));
            }
            return;
        }
        ITransaction transaction = DBFlowUtil.getTransaction(list, OperationType.TYPE_SAVE);
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, transaction, callback);
    }


    @Override
    public FPEntity getFpEntityByWalletIdAndIndex(int walletID,int index) {
        FPEntity fpEntity = SQLite.select().from(FPEntity.class)
                .where(FPEntity_Table.multiWalletEntity_id.eq(walletID))
                .and(FPEntity_Table.index.eq(index))
                .querySingle();
        return fpEntity;
    }

    @Override
    public FPEntity getFpEntityListByWalletIdAndName(int walletID, String fpName) {
        FPEntity fpEntity = SQLite.select().from(FPEntity.class)
                .where(FPEntity_Table.multiWalletEntity_id.eq(walletID))
                .and(FPEntity_Table.name.eq(fpName))
                .querySingle();
        return fpEntity;
    }


    @Override
    public void deleteFpEntityAsync(FPEntity fpEntity,DBCallback dbCallback) {
        DBFlowUtil.execTransactionAsync(GemmaDatabase.class, new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                fpEntity.delete();
            }
        }, dbCallback);
    }

}

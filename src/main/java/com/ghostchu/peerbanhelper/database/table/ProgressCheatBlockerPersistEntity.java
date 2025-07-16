package com.ghostchu.peerbanhelper.database.table;

import com.ghostchu.peerbanhelper.database.dao.impl.ProgressCheatBlockerPersistDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@DatabaseTable(tableName = "pcb_persist_records", daoClass = ProgressCheatBlockerPersistDao.class)
public final class ProgressCheatBlockerPersistEntity {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(canBeNull = false, uniqueCombo = true, indexName = "pcb_persist_address_torrentid")
    private String address;
    @DatabaseField(canBeNull = false, uniqueCombo = true, indexName = "pcb_persist_address_torrentid")
    private String torrentId;
    @DatabaseField(canBeNull = false)
    private double lastReportProgress;
    @DatabaseField
    private long lastReportUploaded;
    @DatabaseField
    private long trackingUploadedIncreaseTotal;
    @DatabaseField(canBeNull = false)
    private int rewindCounter;
    @DatabaseField(canBeNull = false)
    private int progressDifferenceCounter;
    @DatabaseField(canBeNull = false)
    private Timestamp firstTimeSeen;
    @DatabaseField(canBeNull = false, index = true)
    private Timestamp lastTimeSeen;
    @DatabaseField(canBeNull = false)
    private String downloader;
    @DatabaseField(canBeNull = false)
    private Timestamp banDelayWindowEndAt;
    @DatabaseField(canBeNull = false)
    private long fastPcbTestExecuteAt;
    @DatabaseField(canBeNull = false)
    private long lastLastTorrentCompletedProgress;
}

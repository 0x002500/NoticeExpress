package top.orderly.noticeexpress.model;

import java.util.UUID;

/**
 * Represents a notice/announcement in the system.
 */
public class Notice {
    private int id;
    private String title;
    private String publisher;
    private UUID publisherUuid;
    private String content;
    private long timestamp;
    private boolean isPinned;
    private long createdAt;

    public Notice() {
        this.createdAt = System.currentTimeMillis();
        this.timestamp = this.createdAt;
        this.isPinned = false;
    }

    public Notice(int id, String title, String publisher, UUID publisherUuid, String content, long timestamp, boolean isPinned, long createdAt) {
        this.id = id;
        this.title = title;
        this.publisher = publisher;
        this.publisherUuid = publisherUuid;
        this.content = content;
        this.timestamp = timestamp;
        this.isPinned = isPinned;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public UUID getPublisherUuid() {
        return publisherUuid;
    }

    public void setPublisherUuid(UUID publisherUuid) {
        this.publisherUuid = publisherUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publisherUuid=" + publisherUuid +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isPinned=" + isPinned +
                ", createdAt=" + createdAt +
                '}';
    }
}

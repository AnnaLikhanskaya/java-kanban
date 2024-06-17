package model;


import java.time.LocalDateTime;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, long duration, LocalDateTime startTime, Status taskStatus, int epicId) {
        super(name, description, duration, startTime, taskStatus);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                " name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}

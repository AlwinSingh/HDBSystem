package src.model;

import java.time.LocalDate;

public class EnquiryReply {
    private int replyId;
    private String content;
    private LocalDate timestamp;
    private User responder;

    public EnquiryReply(int replyId, String content, User responder) {
        this.replyId = replyId;
        this.content = content;
        this.responder = responder;
        this.timestamp = LocalDate.now();
    }

    public void editReply(String newContent) {
        this.content = newContent;
        this.timestamp = LocalDate.now(); // update timestamp on edit
    }

    public void deleteReply() {
        this.content = "[deleted]";
    }

    // Getters
}

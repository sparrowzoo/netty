package io.netty.example.http.websocketx.server;

import io.netty.buffer.ByteBuf;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Protocol {
    public static final int TEXT_MESSAGE = 0;
    public static final int IMAGE_MESSAGE = 1;
    public static final int CHAT_TYPE_1_2_1 = 0;
    public static final int CHAT_TYPE_1_2_N = 1;
    private int messageType;
    private int charType;
    private int sessionLength;
    private int fromUserId;
    private int targetUserId;
    private String session;
    private int contentLength;
    private String content;

    private String getSession(Integer user1, Integer user2) {
        Integer[] userArray = new Integer[2];
        userArray[0] = user1;
        userArray[1] = user2;
        Arrays.sort(userArray);
        return userArray[0] + "_" + userArray[1];
    }

    public Protocol(ByteBuf content) throws UnsupportedEncodingException {
        this.charType = content.readByte();
        this.messageType = content.readByte();
        this.fromUserId = content.readInt();
        if (this.charType == CHAT_TYPE_1_2_1) {
            this.targetUserId = content.readInt();
            this.session = this.getSession(fromUserId, targetUserId);
        } else {
            this.sessionLength = content.readInt();
            byte[] sessionBytes = new byte[sessionLength];
            content.readBytes(sessionBytes);
            this.session = new String(sessionBytes);
        }
        this.contentLength = content.readInt();
        byte[] contentBytes = new byte[contentLength];
        content.readBytes(contentBytes);
        content.resetReaderIndex();
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(int sessionLength) {
        this.sessionLength = sessionLength;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isOne2One() {
        return this.charType == CHAT_TYPE_1_2_1;
    }

    public boolean isText() {
        return this.messageType == TEXT_MESSAGE;
    }

    public int getTargetUserId() {
        return this.targetUserId;
    }

    @Override public String toString() {
        return "Protocol{" +
            "messageType=" + messageType +
            ", sessionLength=" + sessionLength +
            ", fromUserId=" + fromUserId +
            ", session='" + session + '\'' +
            ", contentLength=" + contentLength +
            ", content='" + content + '\'' +
            '}';
    }
}

package org.metalscraps.eso.lang.kr.bean;

public class ID {

    public ID(String head, String body, String tail) {
        this.head = head;
        this.body = body;
        this.tail = tail;
    }

    public void setHead(String head) {
        this.head = head;
        isFileNameHead = head.substring(0,1).matches("^[a-zA-Z]");
    }

    @Override
    public String toString() { return head+"-"+body+"-"+tail; }

    public boolean isFileNameHead() {
        return isFileNameHead;
    }

    public void setFileNameHead(boolean fileNameHead) {
        isFileNameHead = fileNameHead;
    }

    public String getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    private boolean isFileNameHead;
    private String head, body, tail;
    public static class NotFileNameHead extends Exception {}

}

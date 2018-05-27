package com.ever.ending.interfaces.manipulation;

public interface IOpenClose extends IControllable {
    public boolean isOpen();
    public void close();
    public void open();
    public void setClosed(boolean closed);
}

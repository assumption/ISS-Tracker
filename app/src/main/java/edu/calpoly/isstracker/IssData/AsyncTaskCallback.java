package edu.calpoly.isstracker.IssData;

public interface AsyncTaskCallback {
    void done(IssData issData);
    void timeoutError();
}

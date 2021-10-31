package com.trm.myexercise.utils;

public class Event<T> {
    private T mContent;

    private boolean hasBeenHandled = false;

    public Event(T content) {
        mContent = content;
    }

    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    public T getContentIfNeed() {
        if (hasBeenHandled) {
            return null;
        }

        hasBeenHandled = true;
        return mContent;
    }

    public T peekContent() {
        return mContent;
    }

}

package com.example.contactsapp.ui.authentication;

public class Resource<T> {

    public static class Loading<T> extends Resource<T> {}

    public static class Successful<T> extends Resource<T> {
        private T data;

        public Successful(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class Unsuccessful<T> extends Resource<T> {
        private T data;

        public Unsuccessful(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
}

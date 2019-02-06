package com.example.shakurhassan.menuweather;

public class HttpGetThread extends Thread {

    private OnRequestDoneInterface getter = null;

    public void setGetter(OnRequestDoneInterface getter) {
        this.getter = getter;
    }

    private String urlString;

    private Boolean running = true;

    @Override
    public void run() {
        try {
            while (running){
                if(getter != null){
                    this.getter.requestGetter(urlString);
                }
                sleep(100);
                running = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }
}

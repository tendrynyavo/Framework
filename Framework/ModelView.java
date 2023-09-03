package etu2070.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelView {

    String view, redirect;
    Map<String, Object> data = new HashMap<String, Object>(), session = new HashMap<String, Object>();
    List<String> remove = new ArrayList<>();
    boolean invalidate = false, isJson = false;

    public ModelView sendRedirect(String redirect) {
        this.redirect = redirect;
        return this;
    }

    public String getRedirect() {
        return redirect;
    }

    public boolean isInvalidate() {
        return invalidate;
    }

    public ModelView setInvalidate(boolean invalidate) {
        this.invalidate = invalidate;
        return this;
    }
    
    public List<String> getRemove() {
        return remove;
    }

    public ModelView setRemove(List<String> remove) {
        this.remove = remove;
        return this;
    }

    public boolean isJson() {
        return isJson;
    }

    public ModelView setJson(boolean isJson) {
        this.isJson = isJson;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public ModelView setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public ModelView setSession(Map<String, Object> session) {
        this.session = session;
        return this;
    }

    public String getView() {
        return view;
    }

    public ModelView setView(String view) {
        this.view = "view/" + view + ".jsp";
        return this;
    }

    public ModelView() {}

    public ModelView(String view) {
        this.setView(view);
    }

    public ModelView addItem(String key, Object value) {
        this.getData().put(key, value);
        return this;
    }

    public ModelView addSession(String key, Object value) {
        this.getSession().put(key, value);
        return this;
    }

    public ModelView removeSession(String session) {
        this.getRemove().add(session);       
        return this;
    }

}
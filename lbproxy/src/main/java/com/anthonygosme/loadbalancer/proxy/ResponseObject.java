package com.anthonygosme.loadbalancer.proxy;

import org.apache.http.Header;
import org.apache.http.StatusLine;

public class ResponseObject {
    String body;
    Header[] parameters;
    StatusLine statusLine;
    boolean responseError = false;
}

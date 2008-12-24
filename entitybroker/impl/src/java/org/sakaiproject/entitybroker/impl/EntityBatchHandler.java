/**
 * $Id$
 * $URL$
 * EntityBatchHandler.java - entity-broker - Dec 18, 2008 11:40:39 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.util.http.HttpRESTUtils;


/**
 * This handles batch operations internally as much as possible,
 * the idea is to provide for a standard way to reduce huge numbers of calls down to 1 call to the server
 * which puts the data together into a single response
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class EntityBatchHandler {

    private EntityBrokerManager entityBrokerManager;
    public void setEntityBrokerManager(EntityBrokerManager entityBrokerManager) {
        this.entityBrokerManager = entityBrokerManager;
    }

    private EntityEncodingManager entityEncodingManager;
    public void setEntityEncodingManager(EntityEncodingManager entityEncodingManager) {
        this.entityEncodingManager = entityEncodingManager;
    }

    public void handleBatch(EntityView view, HttpServletRequest req, HttpServletResponse res) {
        // first find out which METHOD we are dealing with
        String method = req.getMethod() == null ? EntityView.Method.GET.name() : req.getMethod().toUpperCase().trim();
        if (EntityView.Method.GET.name().equals(method)) {
            handleBatchGet(view, req, res);
        } else if (EntityView.Method.HEAD.name().equals(method)) {
            throw new java.lang.RuntimeException("Method not implemented yet");
        } else if (EntityView.Method.DELETE.name().equals(method)) {
            throw new java.lang.RuntimeException("Method not implemented yet");
        } else if (EntityView.Method.PUT.name().equals(method)) {
            throw new java.lang.RuntimeException("Method not implemented yet");
        } else if (EntityView.Method.POST.name().equals(method)) {
            throw new java.lang.RuntimeException("Method not implemented yet");
        } else {
            throw new IllegalArgumentException("Unknown HTTP METHOD ("+method+"), cannot continue processing request: " + view);
        }
                
        //HttpRESTUtils.
    }

    /**
     * Handles batching all get operations
     */
    public void handleBatchGet(EntityView view, HttpServletRequest req, HttpServletResponse res) {
        // validate the the refs param
        String[] refs = req.getParameterValues("refs");
        if (refs == null || refs.length == 0) {
            throw new IllegalArgumentException("refs parameter must be set (e.g. /direct/batch.json?refs=/sites/popular,/sites/newest)");
        }
        if (refs.length == 1) {
            // process separated list, assume comma separated
            String separator = req.getParameter("separator");
            if (separator == null || "".equals(separator)) {
                separator = ",";
            }
            String presplit = refs[0];
            refs = presplit.split(separator);
            if (refs == null || refs.length == 0) {
                throw new IllegalStateException("Failure attempting to process the refs ("+presplit+") listing, could not get the final list of refs out by splitting using the separator ("+separator+")");
            }
        }
        if (refs.length <= 0) {
            throw new IllegalArgumentException("refs parameter must be set and there must be at least 1 reference (e.g. /direct/batch.json?refs=/sites/popular,/sites/newest)");
        }
        // loop through all references
        for (int i = 0; i < refs.length; i++) {
            String reference = refs[i];
            if (reference == null || "".equals(reference)) {
                continue; // skip
            }
//            // identify the EB operations
//            EntityReference entityReference = null;
//            if (reference.startsWith(EntityView.DIRECT_PREFIX)) {
//                int loc = reference.indexOf("/", 5);
//                if (loc == -1) {
//                    continue; // skip
//                }
//                reference = reference.substring(loc);
//                // TODO split the URL and query string apart, send URL to parse entity URL, process query string below
//                entityReference = entityBrokerManager.parseReference(reference); //parseEntityURL(reference);
//                // check the prefix is valid
//                Map<String, String> params = HttpRESTUtils.parseURLintoParams(reference);
//                // now execute the request to get the data
//                if (entityReference.getId() == null) {
//                    // space (collection)
//                    
//                } else {
//                    
//                }
//                //entityBrokerManager.getEntityData(ref);
//            }
//            // compile EB responses
            // fire off the URLs to the server and get back responses
            RequestDispatcher dispatcher = req.getRequestDispatcher(reference);
            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(req);
            HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(res);
            try {
                dispatcher.include(requestWrapper, responseWrapper);
            } catch (ServletException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException("died");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException("died");
            }
            
            // compile all the responses
            // create the object to encode into the final response
        }
        // put response, headers, and code into the http response
    }

}

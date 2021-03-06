/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.fuseki.mgt;

import org.apache.jena.fuseki.Fuseki ;
import org.apache.jena.fuseki.server.DataAccessPoint ;
import org.apache.jena.fuseki.server.DataAccessPointRegistry ;
import org.apache.jena.fuseki.server.DataService ;
import org.apache.jena.fuseki.servlets.ActionBase ;
import org.apache.jena.fuseki.servlets.HttpAction ;
import org.apache.jena.fuseki.servlets.ServletOps ;

/** Control/admin request lifecycle */
public abstract class ActionCtl extends ActionBase
{
    private static final long serialVersionUID = 3129123436565022299L;

    protected ActionCtl() { super(Fuseki.adminLog) ; }
    
    @Override
    final
    protected void execCommonWorker(HttpAction action) {
        DataAccessPoint dataAccessPoint ;
        DataService dSrv ;
        
        String datasetUri = mapRequestToDatasetName(action) ;
        if ( datasetUri != null ) {
            dataAccessPoint = DataAccessPointRegistry.get().get(datasetUri) ;
            if ( dataAccessPoint == null ) {
                ServletOps.errorNotFound("Not found: "+datasetUri) ;
                return ;
            }
        }
        else {
            // This is a placeholder when creating new DatasetRefs
            // and also if addressing a container, not a dataset
            dataAccessPoint = null ;
            dSrv = DataService.serviceOnlyDataService() ;
        }
        
        action.setControlRequest(dataAccessPoint, datasetUri) ;
        action.setEndpoint(null, null) ;   // No operation or service name.
        executeAction(action) ;
    }

    protected String mapRequestToDatasetName(HttpAction action) {
        return extractItemName(action) ;
    }

    // Execute - no stats.
    // Intercept point for the UberServlet 
    protected void executeAction(HttpAction action) {
        executeLifecycle(action) ;
    }
    
    // This is the service request lifecycle.
    final
    protected void executeLifecycle(HttpAction action)
    {
        startRequest(action) ;
        try {
            perform(action) ;
        }
        finally { 
            finishRequest(action) ;
        }
    }
    
    final
    protected boolean isContainerAction(HttpAction action) {
        return (action.getDataAccessPoint() == null ) ;
    }
    
    protected abstract void perform(HttpAction action) ;

//    /** Map request to uri in the registry.
//     *  null means no mapping done (passthrough). 
//     */
//    protected String mapRequestToDataset(HttpAction action) 
//    {
//        return ActionLib.mapRequestToDataset(action.request.getRequestURI()) ;
//    }
}

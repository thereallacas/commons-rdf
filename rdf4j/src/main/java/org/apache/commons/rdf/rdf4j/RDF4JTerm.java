/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.apache.commons.rdf.rdf4j;

import org.apache.commons.rdf.api.RDFTerm;
import org.openrdf.model.Value;

/**
 * Marker interface for RDF4J implementations of RDFTerm.
 *  
 */
public interface RDF4JTerm<T extends Value> extends RDFTerm {
	
	/**
	 * Return the RDF4J {@link Value} that this RDFTerm represents.
	 * 
	 * @return
	 */
	public T asValue();
}

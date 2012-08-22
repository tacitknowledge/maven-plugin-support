/* Copyright 2012 Tacit Knowledge
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.tacitknowledge.pluginsupport.util;

import org.apache.maven.artifact.Artifact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Utilities to filter artifacts
 *
 * @author mshort
 * @author marques
 */
public class ArtifactFilter
{
    /** file extension to exclude when filtering by type */
    private static final List<String> EXCLUDED_FILE_TYPES = Arrays.asList("war");

    /** scopes to exclude when filtering by scope */
    private static final List<String> EXCLUDED_SCOPES = Arrays.asList("test");

    /**
     * Filters a collection of artifacts based on scope
     *
     * @param artifactList the list of artifacts to filter
     * @return the scope-filtered list of artifacts
     */
    public static Collection<Artifact> filterByScope(Collection<Artifact> artifactList)
    {
        List<Artifact> filteredArtifactList = new ArrayList<Artifact>();
        for (Artifact artifact : artifactList)
        {
            if (isAllowedScope(artifact))
            {
                filteredArtifactList.add(artifact);
            }
        }
        return filteredArtifactList;
    }

    /**
     * Filters a collection of artifacts based on filetype (packaging)
     *
     * @param artifactList the list of artifacts to filter
     * @return the type-filtered list of artifacts
     */
    public static Collection<Artifact> filterByType(Collection<Artifact> artifactList)
    {
        List<Artifact> filteredArtifactList = new ArrayList<Artifact>();
        for (Artifact artifact : artifactList)
        {
            if (isAllowedType(artifact))
            {
                filteredArtifactList.add(artifact);
            }
        }
        return filteredArtifactList;
    }

    /**
     * Checks if artifact is of an allowed type by examining the filename extension
     *
     * @param artifact the artifact
     * @return true if artifact type is allowed
     */
    private static boolean isAllowedType(Artifact artifact)
    {
        return !EXCLUDED_FILE_TYPES.contains(artifact.getType());
    }

    /**
     * Checks if artifact is in allowed scope
     *
     * @param artifact the artifact
     * @return true if artifact is in scope
     */
    private static boolean isAllowedScope(Artifact artifact)
    {
        return !EXCLUDED_SCOPES.contains(artifact.getScope());
    }
}
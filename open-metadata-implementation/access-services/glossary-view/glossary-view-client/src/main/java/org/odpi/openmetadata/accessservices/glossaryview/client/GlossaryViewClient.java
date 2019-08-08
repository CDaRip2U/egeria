/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.glossaryview.client;

import org.odpi.openmetadata.accessservices.glossaryview.exception.GlossaryViewOmasException;
import org.odpi.openmetadata.accessservices.glossaryview.rest.GlossaryViewEntityDetailResponse;
import org.odpi.openmetadata.commonservices.ffdc.exceptions.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;

/**
 * The Glossary View Open Metadata Access Service (OMAS) provides an interface to query for glossaries, categories and terms.
 * Regarding the paged requests, one can pass null to 'from' and 'size' params in order to use their default values
 */
public class GlossaryViewClient extends OmasClient {

    private static final String PATH_ROOT = "/servers/{0}/open-metadata/access-services/glossary-view/users/{1}";
    private static final String PARAM_DELIMITER = "?";
    private static final String PAGINATION = "from={3}&size={4}";

    private static final String GET_GLOSSARY = PATH_ROOT + "/glossaries/{2}";

    private static final String GET_GLOSSARIES = PATH_ROOT + "/glossaries" + PARAM_DELIMITER + "from={2}&size={3}";
    private static final String GET_TERM_HOME_GLOSSARY = PATH_ROOT + "/terms/{2}/home-glossary";
    private static final String GET_CATEGORY_HOME_GLOSSARY = PATH_ROOT + "/categories/{2}/home-glossary";
    private static final String GET_GLOSSARY_EXTERNAL_GLOSSARIES = PATH_ROOT + "/glossaries/{2}/external-glossaries" + PARAM_DELIMITER + PAGINATION;

    private static final String GET_CATEGORY = PATH_ROOT + "/categories/{2}";
    private static final String GET_CATEGORIES_OF_GLOSSARY = PATH_ROOT + "/glossaries/{2}/categories";
    private static final String GET_SUBCATEGORIES = PATH_ROOT + "/categories/{2}/subcategories";
    private static final String GET_CATEGORY_EXTERNAL_GLOSSARIES = PATH_ROOT + "/categories/{2}/external-glossaries" + PARAM_DELIMITER + PAGINATION;

    private static final String GET_TERM = PATH_ROOT + "/terms/{2}";
    private static final String GET_TERMS_OF_GLOSSARY = PATH_ROOT + "/glossaries/{2}/terms";
    private static final String GET_TERMS_OF_CATEGORY = PATH_ROOT + "/categories/{2}/terms";
    private static final String GET_TERM_EXTERNAL_GLOSSARIES = PATH_ROOT + "/terms/{2}/external-glossaries" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_RELATED_TERMS = PATH_ROOT + "/terms/{2}/see-also" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_SYNONYMS = PATH_ROOT + "/terms/{2}/synonyms" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_ANTONYMS = PATH_ROOT + "/terms/{2}/antonyms" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_PREFERRED_TERMS = PATH_ROOT + "/terms/{2}/preferred-terms" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_REPLACEMENT_TERMS = PATH_ROOT + "/terms/{2}/replacement-terms" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_TRANSLATIONS = PATH_ROOT + "/terms/{2}/translations-terms" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_IS_A = PATH_ROOT + "/terms/{2}/is-a" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_VALID_VALUES = PATH_ROOT + "/terms/{2}/valid-values" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_USED_IN_CONTEXTS = PATH_ROOT + "/terms/{2}/used-in-contexts" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_ASSIGNED_ELEMENTS = PATH_ROOT + "/terms/{2}/assigned-elements" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_ATTRIBUTES = PATH_ROOT + "/terms/{2}/attributes" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_SUBTYPES = PATH_ROOT + "/terms/{2}/subtypes" + PARAM_DELIMITER + PAGINATION;
    private static final String GET_TYPES = PATH_ROOT + "/terms/{2}/types" + PARAM_DELIMITER + PAGINATION;

    /**
     * Create a new client that passes userId and password in each HTTP request.  This is the
     * userId/password of the calling server.  The end user's userId is sent on each request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformRootURL the network address of the server running the OMAS REST servers
     * @param userId caller's userId embedded in all HTTP requests
     * @param password caller's userId embedded in all HTTP requests
     *
     * @throws InvalidParameterException null URL or server name
     */
    public GlossaryViewClient(String serverName, String serverPlatformRootURL, String userId, String password) throws
            org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException {
        super(serverName, serverPlatformRootURL, userId, password);
    }

    /**
     * Extract all glossary definitions
     *
     * @param userId calling user
     * @param serverName instance to call
     *
     * @return EntityDetailResponse glossaries
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getAllGlossaries(String serverName, String userId, Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleEntitiesPagedResponse("getAllGlossaries", GET_GLOSSARIES, serverName, userId, from, size);
    }

    /**
     * Extract a glossary definition
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param glossaryGUID glossary GUID
     *
     * @return EntityDetailResponse glossary
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getGlossary(String serverName, String userId, String glossaryGUID)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getSingleEntityResponse("getGlossary", GET_GLOSSARY, serverName, userId, glossaryGUID);
    }

    /**
     * Extract a term's home glossary
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse glossary
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getTermHomeGlossary(String serverName, String userId, String termGUID)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getSingleEntityResponse("getTermHomeGlossary", GET_TERM_HOME_GLOSSARY, serverName, userId, termGUID);
    }

    /**
     * Extract a category's home glossary
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param categoryGUID category GUID
     *
     * @return EntityDetailResponse glossary
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getCategoryHomeGlossary(String serverName, String userId, String categoryGUID)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getSingleEntityResponse("getCategoryHomeGlossary", GET_CATEGORY_HOME_GLOSSARY, serverName, userId, categoryGUID);
    }

    /**
     * Extract a glossaries's external glossaries
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param glossaryGUID glossary GUID
     *
     * @return EntityDetailResponse external glossary links
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getExternalGlossariesOfGlossary(String serverName, String userId, String glossaryGUID,
                                                                            Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getExternalGlossariesOfGlossary", GET_GLOSSARY_EXTERNAL_GLOSSARIES,
                serverName, userId, glossaryGUID, from, size);
    }

    /**
     * Extract a category definition
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param categoryGUID category GUID
     *
     * @return EntityDetailResponse category
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getCategory(String serverName, String userId, String categoryGUID)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getSingleEntityResponse("getCategory", GET_CATEGORY, serverName, userId, categoryGUID);
    }

    /**
     * Extract categories within a glossary
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param glossaryGUID glossary GUID
     *
     * @return EntityDetailResponse categories
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getCategories(String serverName, String userId, String glossaryGUID,
                                                          Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getCategories", GET_CATEGORIES_OF_GLOSSARY, serverName, userId,
                glossaryGUID, from, size);
    }

    /**
     * Extract subcategories of a category
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param categoryGUID category GUID
     *
     * @return EntityDetailResponse subcategories
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getSubcategories(String serverName, String userId, String categoryGUID,
                                                             Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getSubcategories", GET_SUBCATEGORIES, serverName,
                userId, categoryGUID, from, size);
    }

    /**
     * Extract a category's external glossaries
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param categoryGUID category GUID
     *
     * @return EntityDetailResponse external glossary links
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getExternalGlossariesOfCategory(String serverName, String userId, String categoryGUID,
                                                                            Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getExternalGlossariesOfCategory", GET_CATEGORY_EXTERNAL_GLOSSARIES,
                serverName, userId, categoryGUID, from, size);
    }

    /**
     * Extract a term definition
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse term
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getTerm(String serverName, String userId, String termGUID)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getSingleEntityResponse("getTerm", GET_TERM, serverName, userId, termGUID);
    }

    /**
     * Extract terms within a glossary
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param glossaryGUID glossary GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getTermsOfGlossary(String serverName, String userId, String glossaryGUID,
                                                               Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getTermsOfGlossary", GET_TERMS_OF_GLOSSARY, serverName,
                userId, glossaryGUID, from, size);
    }

    /**
     * Extract terms within a category
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param categoryGUID category GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getTermsOfCategory(String serverName, String userId, String categoryGUID,
                                                               Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getTermsOfCategory", GET_TERMS_OF_CATEGORY, serverName, userId,
                categoryGUID, from, size);
    }

    /**
     * Extract a term's external glossaries
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse external glossary links
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getExternalGlossariesOfTerm(String serverName, String userId, String termGUID,
                                                                        Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getExternalGlossariesOfTerm", GET_TERM_EXTERNAL_GLOSSARIES,
                serverName, userId, termGUID, from, size);
    }

    /**
     * Extract related terms
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getRelatedTerms(String serverName, String userId, String termGUID,
                                                            Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getRelatedTerms", GET_RELATED_TERMS, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract synonyms
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getSynonyms(String serverName, String userId, String termGUID,
                                                            Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getSynonyms", GET_SYNONYMS, serverName, userId,
                termGUID, from, size);
    }

    /**
     * Extract antonyms
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getAntonyms(String serverName, String userId, String termGUID,
                                                        Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getAntonyms", GET_ANTONYMS, serverName, userId,
                termGUID, from, size);
    }

    /**
     * Extract preferred terms
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getPreferredTerms(String serverName, String userId, String termGUID,
                                                              Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getPreferredTerms", GET_PREFERRED_TERMS, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract replacement terms
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getReplacementTerms(String serverName, String userId, String termGUID,
                                                                Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getReplacementTerms", GET_REPLACEMENT_TERMS, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract translations
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getTranslations(String serverName, String userId, String termGUID,
                                                            Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getTranslations", GET_TRANSLATIONS, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract "is-a" terms
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getIsA(String serverName, String userId, String termGUID,
                                                   Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getIsA", GET_IS_A, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract valid values
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getValidValues(String serverName, String userId, String termGUID,
                                                           Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getValidValues", GET_VALID_VALUES, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract "used-in-contexts" terms
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getUsedInContexts(String serverName, String userId, String termGUID,
                                                              Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getUsedInContexts", GET_USED_IN_CONTEXTS, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract assigned elements
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getAssignedElements(String serverName, String userId, String termGUID,
                                                                Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getAssignedElements", GET_ASSIGNED_ELEMENTS, serverName,
                userId, termGUID, from, size);
    }

    /**
     * Extract attributes
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getAttributes(String serverName, String userId, String termGUID,
                                                          Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getAttributes", GET_ATTRIBUTES, serverName, userId,
                termGUID, from, size);
    }

    /**
     * Extract subtypes
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getSubtypes(String serverName, String userId, String termGUID,
                                                        Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getSubtypes", GET_SUBTYPES, serverName, userId,
                termGUID, from, size);
    }

    /**
     * Extract types
     *
     * @param userId calling user
     * @param serverName instance to call
     * @param termGUID term GUID
     *
     * @return EntityDetailResponse terms
     *
     * @throws PropertyServerException if a problem occurs while serving the request
     * @throws InvalidParameterException if parameter validation fails
     * @throws GlossaryViewOmasException if a problem occurs on the omas backend
     */
    public GlossaryViewEntityDetailResponse getTypes(String serverName, String userId, String termGUID,
                                                     Integer from, Integer size)
            throws PropertyServerException, InvalidParameterException, GlossaryViewOmasException {

        return getMultipleRelatedEntitiesPagedResponse("getTypes", GET_TYPES, serverName, userId, termGUID,
                from, size);
    }

}

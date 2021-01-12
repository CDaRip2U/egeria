/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.assetlineage.handlers;

import org.apache.commons.collections4.CollectionUtils;
import org.odpi.openmetadata.accessservices.assetlineage.event.AssetLineageEventType;
import org.odpi.openmetadata.accessservices.assetlineage.model.AssetContext;
import org.odpi.openmetadata.accessservices.assetlineage.model.GraphContext;
import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.repositoryhandler.RepositoryHandler;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.OCFCheckedExceptionBase;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityDetail;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.TypeDefGallery;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.RepositoryErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.ASSET_LINEAGE_OMAS;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.ASSET_SCHEMA_TYPE;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.ATTRIBUTE_FOR_SCHEMA;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.COMPLEX_SCHEMA_TYPE;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.CONNECTION;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.CONNECTION_ENDPOINT;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.CONNECTION_TO_ASSET;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.DATABASE;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.DATA_CONTENT_FOR_DATA_SET;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.DATA_FILE;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.FILE_FOLDER;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.FOLDER_HIERARCHY;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.GUID_PARAMETER;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.LINEAGE_MAPPING;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.NESTED_FILE;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.NESTED_SCHEMA_ATTRIBUTE;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.RELATIONAL_COLUMN;
import static org.odpi.openmetadata.accessservices.assetlineage.util.AssetLineageConstants.TABULAR_COLUMN;

/**
 * The Asset Context handler provides methods to build graph context for assets that has been created.
 */
public class AssetContextHandler {

    private final RepositoryHandler repositoryHandler;
    private final InvalidParameterHandler invalidParameterHandler;
    private final HandlerHelper handlerHelper;
    private final List<String> supportedZones;


    /**
     * @param invalidParameterHandler    handler for invalid parameters
     * @param repositoryHelper           helper used by the converters
     * @param repositoryHandler          handler for calling the repository services
     * @param supportedZones             configurable list of zones that Asset Lineage is allowed to retrieve Assets from
     * @param lineageClassificationTypes lineage classification list
     */
    public AssetContextHandler(InvalidParameterHandler invalidParameterHandler, OMRSRepositoryHelper repositoryHelper,
                               RepositoryHandler repositoryHandler, List<String> supportedZones, Set<String> lineageClassificationTypes) {
        this.invalidParameterHandler = invalidParameterHandler;
        this.repositoryHandler = repositoryHandler;
        this.handlerHelper = new HandlerHelper(invalidParameterHandler, repositoryHelper, repositoryHandler, lineageClassificationTypes);
        this.supportedZones = supportedZones;
    }

    /**
     * @param userId         the user id
     * @param entityTypeName the name of the entity type
     *
     * @return the existing list of glossary terms available in the repository
     *
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException    something went wrong with the REST call stack.
     */
    public List<EntityDetail> getEntitiesByTypeName(String userId, String entityTypeName) throws UserNotAuthorizedException, PropertyServerException {
        final String methodName = "getEntitiesByTypeName";

        String typeDefGUID = handlerHelper.getTypeByName(userId, entityTypeName);

        return repositoryHandler.getEntitiesByType(userId, typeDefGUID, 0, 0, methodName);
    }

    /**
     * @param userId         the user id
     * @param guid           the guid of the entity
     * @param entityTypeName the name of the entity type
     *
     * @return the existing list of glossary terms available in the repository
     *
     * @throws InvalidParameterException  one of the parameters is null or invalid.
     * @throws UserNotAuthorizedException user not authorized to issue this request.
     * @throws PropertyServerException    problem retrieving the entity.
     */
    public EntityDetail getEntityByTypeAndGuid(String userId, String guid, String entityTypeName) throws InvalidParameterException,
                                                                                                         PropertyServerException,
                                                                                                         UserNotAuthorizedException {
        return handlerHelper.getEntityDetails(userId, guid, entityTypeName);
    }

    /**
     * Gets asset context.
     *
     * @param userId       the user id
     * @param entityDetail the entity for which the context is build
     *
     * @return the asset context
     */
    public Map<String, Set<GraphContext>> buildSchemaElementContext(String userId, EntityDetail entityDetail) throws OCFCheckedExceptionBase {
        final String methodName = "buildSchemaElementContext";

        invalidParameterHandler.validateGUID(entityDetail.getGUID(), GUID_PARAMETER, methodName);
        invalidParameterHandler.validateAssetInSupportedZone(entityDetail.getGUID(), GUID_PARAMETER,
                handlerHelper.getAssetZoneMembership(entityDetail.getClassifications()), supportedZones, ASSET_LINEAGE_OMAS, methodName);

        Map<String, Set<GraphContext>> context = new HashMap<>();
        Set<GraphContext> columnContext = new HashSet<>();

        final String typeDefName = entityDetail.getType().getTypeDefName();

        switch (typeDefName) {

            case TABULAR_COLUMN:
                List<Relationship> attributeForSchemaRelationship = handlerHelper.addContextForRelationships(userId, entityDetail,
                        ATTRIBUTE_FOR_SCHEMA, columnContext);
                if (CollectionUtils.isEmpty(attributeForSchemaRelationship)) {
                    //no AttributeForSchema found, context for column will be empty
                    return Collections.emptyMap();
                }
                EntityDetail schemaType = handlerHelper.getEntityAtTheEnd(userId, entityDetail.getGUID(), attributeForSchemaRelationship.get(0));

                List<Relationship> assetSchemaType = handlerHelper.addContextForRelationships(userId, schemaType, ASSET_SCHEMA_TYPE, columnContext);

                context.put(AssetLineageEventType.COLUMN_CONTEXT_EVENT.getEventTypeName(), columnContext);

                if (CollectionUtils.isNotEmpty(assetSchemaType)) {
                    //build the context for DataFile only if AssetSchemaType relationship is found
                    EntityDetail dataFile = handlerHelper.getEntityAtTheEnd(userId, schemaType.getGUID(), assetSchemaType.get(0));
                    context.put(AssetLineageEventType.ASSET_CONTEXT_EVENT.getEventTypeName(), buildDataFileContext(userId, dataFile));
                }
                break;

            case RELATIONAL_COLUMN:
                List<Relationship> nestedSchema = handlerHelper.addContextForRelationships(userId, entityDetail, NESTED_SCHEMA_ATTRIBUTE,
                        columnContext);

                if (CollectionUtils.isEmpty(nestedSchema)) {
                    //no NestedSchemaAttribute found, context for column will be empty
                    return Collections.emptyMap();
                }

                EntityDetail relationalTable = handlerHelper.getEntityAtTheEnd(userId, entityDetail.getGUID(), nestedSchema.get(0));

                context.put(AssetLineageEventType.COLUMN_CONTEXT_EVENT.getEventTypeName(), columnContext);
                context.put(AssetLineageEventType.ASSET_CONTEXT_EVENT.getEventTypeName(), buildRelationalTableContext(userId, relationalTable));

                break;
        }

        return context;
    }

    private Set<GraphContext> buildRelationalTableContext(String userId, EntityDetail entityDetail) throws OCFCheckedExceptionBase {
        Set<GraphContext> context = new HashSet<>();

        List<Relationship> attributeForSchemaRelationship = handlerHelper.addContextForRelationships(userId, entityDetail, ATTRIBUTE_FOR_SCHEMA,
                context);
        if (CollectionUtils.isEmpty(attributeForSchemaRelationship)) {
            //no AttributeForSchema found, context for table will be empty
            return context;
        }

        EntityDetail schemaType = handlerHelper.getEntityAtTheEnd(userId, entityDetail.getGUID(), attributeForSchemaRelationship.get(0));
        List<Relationship> assetSchemaTypeRelationship = handlerHelper.addContextForRelationships(userId, schemaType, ASSET_SCHEMA_TYPE,
                context);
        if (CollectionUtils.isEmpty(assetSchemaTypeRelationship)) {
            return context;
        }

        EntityDetail deployedSchemaType = handlerHelper.getEntityAtTheEnd(userId, schemaType.getGUID(), assetSchemaTypeRelationship.get(0));
        List<Relationship> dataContentForDataSetRelationship = handlerHelper.addContextForRelationships(userId, deployedSchemaType,
                DATA_CONTENT_FOR_DATA_SET, context);
        if (CollectionUtils.isEmpty(dataContentForDataSetRelationship)) {
            return context;
        }

        EntityDetail database = handlerHelper.getEntityAtTheEnd(userId, deployedSchemaType.getGUID(), dataContentForDataSetRelationship.get(0));
        addConnectionToAssetContext(userId, database, context);

        return context;
    }

    private void addConnectionToAssetContext(String userId, EntityDetail asset, Set<GraphContext> context) throws OCFCheckedExceptionBase {
        List<Relationship> contentToAssetRelationship = handlerHelper.addContextForRelationships(userId, asset, CONNECTION_TO_ASSET, context);

        if (CollectionUtils.isEmpty(contentToAssetRelationship)) {
            return;
        }

        EntityDetail connection = handlerHelper.getEntityAtTheEnd(userId, asset.getGUID(), contentToAssetRelationship.get(0));
        handlerHelper.addContextForRelationships(userId, connection, CONNECTION_ENDPOINT, context);
    }

    private Set<GraphContext> buildDataFileContext(String userId, EntityDetail entityDetail) throws OCFCheckedExceptionBase {
        Set<GraphContext> context = new HashSet<>();

        List<Relationship> nestedFileRelationship = handlerHelper.addContextForRelationships(userId, entityDetail, NESTED_FILE, context);

        if (CollectionUtils.isEmpty(nestedFileRelationship)) {
            return context;
        }
        EntityDetail fileFolder = handlerHelper.getEntityAtTheEnd(userId, entityDetail.getGUID(), nestedFileRelationship.get(0));

        context.addAll(buildContextForFileFolder(userId, fileFolder));

        return context;
    }

    private Set<GraphContext> buildContextForFileFolder(String userId, EntityDetail entityDetail) throws OCFCheckedExceptionBase {
        Set<GraphContext> context = new HashSet<>();

        if (entityDetail == null) {
            return context;
        }

        List<Relationship> folderHierarchyRelationship = handlerHelper.addContextForRelationships(userId, entityDetail, FOLDER_HIERARCHY, context);
        if (CollectionUtils.isNotEmpty(folderHierarchyRelationship)) {
            //recursively build the nested folder structure
            buildContextForFileFolder(userId, handlerHelper.getEntityAtTheEnd(userId, entityDetail.getGUID(),
                    folderHierarchyRelationship.get(0)));
        } else {
            // build the context for the Connection
            addConnectionToAssetContext(userId, entityDetail, context);
        }

        return context;
    }


    /**
     * Build the context for the asset
     *
     * @param userId       the user id
     * @param entityDetail the entity for which the context is build
     * @param assetContext the asset context to be updated
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private void buildSchemaElementContext(String userId, EntityDetail entityDetail, AssetContext assetContext) throws OCFCheckedExceptionBase {
        final String typeDefName = entityDetail.getType().getTypeDefName();

        addLineageMappings(userId, entityDetail, typeDefName, assetContext);

        List<EntityDetail> tableTypeEntities = buildGraphByRelationshipType(userId, entityDetail, ATTRIBUTE_FOR_SCHEMA, typeDefName, assetContext);

        if (tableTypeEntities.isEmpty()) {
            tableTypeEntities = buildGraphByRelationshipType(userId, entityDetail, NESTED_SCHEMA_ATTRIBUTE, typeDefName, assetContext);
        }
        for (EntityDetail schemaTypeEntity : tableTypeEntities) {
            if (isComplexSchemaType(userId, schemaTypeEntity.getType().getTypeDefName())) {
                setAssetDetails(userId, schemaTypeEntity, assetContext);
            } else {
                Optional<EntityDetail> first = tableTypeEntities.stream().findFirst();
                if (first.isPresent()) {
                    buildSchemaElementContext(userId, first.get(), assetContext);
                }
            }
        }
    }

    /**
     * Add lineage mappings to asset context
     *
     * @param userId       the user id
     * @param entityDetail the entity object for which
     * @param typeDefName  the entity type name
     * @param assetContext the asset context to be updated
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private void addLineageMappings(String userId, EntityDetail entityDetail, String typeDefName, AssetContext assetContext) throws
                                                                                                                             OCFCheckedExceptionBase {
        List<Relationship> relationships = handlerHelper.getRelationshipsByType(userId, entityDetail.getGUID(), LINEAGE_MAPPING, typeDefName);
        for (Relationship relationship : relationships) {
            handlerHelper.buildGraphEdgeByRelationship(userId, entityDetail, relationship, assetContext);
        }
    }

    /**
     * Add the ends of the relationship to the asset context
     *
     * @param userId           the user id
     * @param entityDetail     the entity object
     * @param relationshipType the relationship name
     * @param typeDefName      the entity type name
     * @param assetContext     the asset context to be updated
     *
     * @return the list of end entities that were added to the context
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private List<EntityDetail> buildGraphByRelationshipType(String userId, EntityDetail entityDetail,
                                                            String relationshipType, String typeDefName, AssetContext assetContext) throws
                                                                                                                                    OCFCheckedExceptionBase {
        handlerHelper.addLineageClassificationToContext(entityDetail, assetContext);

        List<Relationship> relationships = handlerHelper.getRelationshipsByType(userId, entityDetail.getGUID(), relationshipType, typeDefName);

        if (entityDetail.getType().getTypeDefName().equals(FILE_FOLDER)) {
            relationships = relationships.stream().filter(relationship ->
                    relationship.getEntityTwoProxy().getGUID().equals(entityDetail.getGUID())).collect(Collectors.toList());
        }

        List<EntityDetail> entityDetails = new ArrayList<>();
        for (Relationship relationship : relationships) {

            EntityDetail endEntity = handlerHelper.buildGraphEdgeByRelationship(userId, entityDetail, relationship, assetContext);
            if (endEntity == null) return Collections.emptyList();

            entityDetails.add(endEntity);
        }
        return entityDetails;
    }

    /**
     * Add asset details to the context
     *
     * @param userId            the user id
     * @param complexSchemaType the complex schema type entity
     * @param assetContext      the asset context to be updated
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private void setAssetDetails(String userId, EntityDetail complexSchemaType, AssetContext assetContext) throws OCFCheckedExceptionBase {
        List<EntityDetail> assetEntity = buildGraphByRelationshipType(userId,
                complexSchemaType, ASSET_SCHEMA_TYPE, complexSchemaType.getType().getTypeDefName(), assetContext);
        Optional<EntityDetail> first = assetEntity.stream().findFirst();
        if (first.isPresent()) {
            buildAsset(userId, first.get(), assetContext);

        }
    }

    /**
     * Add asset schema type for the asset
     *
     * @param userId       the user id
     * @param entityDetail the data set entity
     * @param assetContext the asset context to be updated
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private void buildAsset(String userId, EntityDetail entityDetail, AssetContext assetContext) throws OCFCheckedExceptionBase {
        final String typeDefName = entityDetail.getType().getTypeDefName();

        String relationshipType = typeDefName.equals(DATA_FILE) ? NESTED_FILE : DATA_CONTENT_FOR_DATA_SET;
        List<EntityDetail> entityDetails = buildGraphByRelationshipType(userId, entityDetail, relationshipType, typeDefName, assetContext);
        if (CollectionUtils.isEmpty(entityDetails)) {
            return;
        }
        addContextForEndpoints(userId, assetContext, entityDetails.toArray(new EntityDetail[0]));
    }

    /**
     * Add endpoints for the asset to the context
     *
     * @param userId        the user id
     * @param assetContext  the asset context to be updated
     * @param entityDetails the list of endpoints
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private void addContextForEndpoints(String userId, AssetContext assetContext, EntityDetail... entityDetails) throws OCFCheckedExceptionBase {
        for (EntityDetail entityDetail : entityDetails) {
            if (entityDetail != null) {
                if (entityDetail.getType().getTypeDefName().equals(DATABASE)) {
                    addContextForConnections(userId, entityDetail, assetContext);
                } else {
                    addContextFolderHierarchy(userId, entityDetail, assetContext);
                }
            }
        }
    }

    /**
     * Add connection to the asset context
     *
     * @param userId       the user id
     * @param entityDetail the database entity
     * @param assetContext the asset context to be updated
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private void addContextForConnections(String userId, EntityDetail entityDetail, AssetContext assetContext) throws OCFCheckedExceptionBase {

        List<EntityDetail> connections = buildGraphByRelationshipType(userId, entityDetail, CONNECTION_TO_ASSET, DATABASE, assetContext);

        if (!connections.isEmpty()) {
            for (EntityDetail entity : connections) {
                buildGraphByRelationshipType(userId, entity, CONNECTION_ENDPOINT, CONNECTION, assetContext);
            }
        }
    }

    /**
     * Add folder hierarchy to the asset context
     *
     * @param userId       the user id
     * @param entityDetail the folder entity
     * @param assetContext the asset context to be updated
     *
     * @throws OCFCheckedExceptionBase checked exception for reporting errors found when using OCF connectors
     */
    private void addContextFolderHierarchy(String userId, EntityDetail entityDetail, AssetContext assetContext) throws OCFCheckedExceptionBase {

        List<EntityDetail> connections = buildGraphByRelationshipType(userId, entityDetail,
                CONNECTION_TO_ASSET, entityDetail.getType().getTypeDefName(), assetContext);

        Optional<EntityDetail> connection = connections.stream().findFirst();
        if (connection.isPresent()) {
            buildGraphByRelationshipType(userId, entityDetail, CONNECTION_ENDPOINT, CONNECTION, assetContext);
        }

        Optional<EntityDetail> nestedFolder = buildGraphByRelationshipType(userId, entityDetail, FOLDER_HIERARCHY, FILE_FOLDER, assetContext)
                .stream()
                .findFirst();

        if (nestedFolder.isPresent()) {
            addContextFolderHierarchy(userId, nestedFolder.get(), assetContext);
        }
    }

    /**
     * Checks if the type is a Complex Schema Type
     *
     * @param userId      the user id
     * @param typeDefName the type name
     *
     * @return true if the given type is a complex schema type
     *
     * @throws RepositoryErrorException                                                           there is a problem communicating with the
     *                                                                                            metadata repository
     * @throws org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException  one of the parameters is invalid
     * @throws org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException the user is not authorized to issue this request
     */
    private boolean isComplexSchemaType(String userId, String typeDefName) throws RepositoryErrorException,
                                                                                  org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException,
                                                                                  org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException {
        TypeDefGallery allTypes = repositoryHandler.getMetadataCollection().getAllTypes(userId);
        return allTypes.getTypeDefs().stream().anyMatch(t -> t.getName().equals(typeDefName) && t.getSuperType().getName().equals(COMPLEX_SCHEMA_TYPE));
    }
}

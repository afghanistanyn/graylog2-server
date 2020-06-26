package org.graylog.security;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.graylog2.utilities.GRNRegistry;
import org.graylog2.shared.security.RestPermissions;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class BuiltinRoles {
    private static final ImmutableMap<String, RoleDTO> ROLES;

    public final static String ROLE_ENTITY_VIEWER = "grn::::role:54e3deadbeefdeadbeef0000";
    public final static String ROLE_ENTITY_MANAGER = "grn::::role:54e3deadbeefdeadbeef0001";
    public final static String ROLE_ENTITY_OWNER = "grn::::role:54e3deadbeefdeadbeef0002";

    public final static String ROLE_COLLECTION_CREATOR = "grn::::role:54e3deadbeefdeadbeef1001";
    public final static String ROLE_DASHBOARD_CREATOR = "grn::::role:54e3deadbeefdeadbeef1002";
    public final static String ROLE_STREAM_CREATOR = "grn::::role:54e3deadbeefdeadbeef1003";

    private static final GRNRegistry GRN_REGISTRY = GRNRegistry.createWithBuiltinTypes();

    static {

        ROLES = ImmutableMap.<String, RoleDTO>builder()
                .put(ROLE_ENTITY_VIEWER, RoleDTO.create(
                        GRN_REGISTRY.parse(ROLE_ENTITY_VIEWER).entity(),
                        "Viewer",
                        ImmutableSet.of(
                                RestPermissions.STREAMS_READ,
                                RestPermissions.DASHBOARDS_READ
                                // TODO: Add missing collection permissions
                        )
                ))
                .put(ROLE_ENTITY_MANAGER, RoleDTO.create(
                        GRN_REGISTRY.parse(ROLE_ENTITY_MANAGER).entity(),
                        "Manager",
                        ImmutableSet.of(
                                RestPermissions.STREAMS_READ,
                                RestPermissions.STREAMS_EDIT,
                                RestPermissions.STREAMS_CHANGESTATE,
                                RestPermissions.DASHBOARDS_READ,
                                RestPermissions.DASHBOARDS_EDIT
                                // TODO: Add missing collection permissions
                        )
                ))
                .put(ROLE_ENTITY_OWNER, RoleDTO.create(
                        GRN_REGISTRY.parse(ROLE_ENTITY_OWNER).entity(),
                        "Owner",
                        ImmutableSet.of(
                                RestPermissions.ENTITY_OWN,
                                RestPermissions.STREAMS_READ,
                                RestPermissions.STREAMS_EDIT,
                                RestPermissions.STREAMS_CHANGESTATE,
                                RestPermissions.DASHBOARDS_READ,
                                RestPermissions.DASHBOARDS_EDIT
                                // TODO: Add missing collection permissions
                        )
                ))
                .put(ROLE_COLLECTION_CREATOR, RoleDTO.create(
                        GRN_REGISTRY.parse(ROLE_COLLECTION_CREATOR).entity(),
                        "Collection Creator",
                        // TODO this is an enterprise role, do we want pluggable roles?
                        // TODO or another solution?
                        // ImmutableSet.of(AdditionalRestPermissions.COLLECTION_CREATE)
                        ImmutableSet.of("collections:create")
                ))
                .put(ROLE_DASHBOARD_CREATOR, RoleDTO.create(
                        GRN_REGISTRY.parse(ROLE_DASHBOARD_CREATOR).entity(),
                        "Dashboard Creator",
                        ImmutableSet.of(RestPermissions.DASHBOARDS_CREATE)
                ))
                .put(ROLE_STREAM_CREATOR, RoleDTO.create(
                        GRN_REGISTRY.parse(ROLE_STREAM_CREATOR).entity(),
                        "Stream Creator",
                        ImmutableSet.of(RestPermissions.STREAMS_CREATE)
                ))
                .build();
    }

    public static ImmutableSet<RoleDTO> allSharingRoles() {
        return ImmutableSet.of(
                ROLES.get(ROLE_ENTITY_VIEWER),
                ROLES.get(ROLE_ENTITY_MANAGER),
                ROLES.get(ROLE_ENTITY_OWNER)
        );
    }

    public Optional<RoleDTO> get(String grn) {
        return Optional.ofNullable(ROLES.get(grn));
    }
}

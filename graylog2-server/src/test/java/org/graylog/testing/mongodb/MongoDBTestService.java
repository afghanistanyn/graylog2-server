/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.testing.mongodb;

import org.graylog2.configuration.MongoDbConfiguration;
import org.graylog2.database.MongoConnection;
import org.graylog2.database.MongoConnectionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * Provides a MongoDB database service for tests.
 */
public class MongoDBTestService implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBTestService.class);

    private static final String DEFAULT_DATABASE_NAME = "graylog";
    public static final String DEFAULT_VERSION = MongoDBContainer.DEFAULT_VERSION;

    private final MongoDBContainer container;
    private MongoConnectionImpl mongoConnection;

    public static String defaultVersion() {
        return MongoDBContainer.DEFAULT_VERSION;
    }

    /**
     * Create service instance with default settings.
     *
     * @return the service instance
     */
    public static MongoDBTestService create() {
        return new MongoDBTestService(MongoDBContainer.create());
    }

    /**
     * Create service instance with the given version.
     *
     * @return the service instance
     */
    public static MongoDBTestService create(String version) {
        return new MongoDBTestService(MongoDBContainer.create(version));
    }

    /**
     * Create service instance with the given network.
     *
     * @return the service instance
     */
    public static MongoDBTestService create(Network network) {
        return new MongoDBTestService(MongoDBContainer.create(network));
    }

    /**
     * Create service instance with the given version and network.
     *
     * @return the service instance
     */
    public static MongoDBTestService create(String version, Network network) {
        return new MongoDBTestService(MongoDBContainer.create(version, network));
    }

    private MongoDBTestService(MongoDBContainer container) {
        this.container = requireNonNull(container, "container cannot be null");
    }

    /**
     * Starts the service and establishes a client connection. Will do nothing if service is already running.
     */
    public void start() {
        if (container.isRunning()) {
            return;
        }

        LOG.debug("Attempting to start container for image: {}", container.getDockerImageName());

        container.start();
        LOG.debug("Started container: {}", container.infoString());

        final MongoDbConfiguration mongoConfiguration = new MongoDbConfiguration();
        mongoConfiguration.setUri(uri());

        this.mongoConnection = new MongoConnectionImpl(mongoConfiguration);
        this.mongoConnection.connect();
        this.mongoConnection.getMongoDatabase().drop();
    }

    /**
     * Close (shutdown) the service.
     *
     * @throws Exception when closing the service fails
     */
    @Override
    public void close() throws Exception {
        container.close();
    }

    /**
     * Returns the established {@link MongoConnection} object.
     *
     * @return the connection object
     */
    public MongoConnection mongoConnection() {
        return requireNonNull(mongoConnection, "mongoConnection not initialized yet");
    }

    /**
     * Drops the configured database.
     */
    public void dropDatabase() {
        LOG.debug("Dropping database {}", mongoConnection().getMongoDatabase().getName());
        mongoConnection().getMongoDatabase().drop();
    }

    /**
     * Returns the IP address of the database instance.
     *
     * @return the IP address
     */
    public String ipAddress() {
        return container.getContainerIpAddress();
    }

    /**
     * Returns the port of the database instance.
     *
     * @return the port
     */
    public int port() {
        return container.getFirstMappedPort();
    }

    /**
     * Returns the service instance ID.
     *
     * @return the instance ID
     */
    public String instanceId() {
        return container.getContainerId();
    }

    public static String internalUri() {
        return uriWithHostAndPort(MongoDBContainer.NETWORK_ALIAS, MongoDBContainer.MONGODB_PORT);
    }

    private String uri() {
        return uriWithHostAndPort(ipAddress(), port());
    }

    private static String uriWithHostAndPort(String hostname, int port) {
        return String.format(Locale.US, "mongodb://%s:%d/%s", hostname, port, DEFAULT_DATABASE_NAME);
    }
}
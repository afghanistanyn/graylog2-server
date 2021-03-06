import React from 'react';
import createReactClass from 'create-react-class';
import Reflux from 'reflux';
import URI from 'urijs';

import URLUtils from 'util/URLUtils';
import StoreProvider from 'injection/StoreProvider';
import { DocumentTitle, ExternalLinkButton, PageHeader, Spinner } from 'components/common';
import { NodesList } from 'components/nodes';

const CurrentUserStore = StoreProvider.getStore('CurrentUser');
const NodesStore = StoreProvider.getStore('Nodes');

const GLOBAL_API_BROWSER_URL = '/api-browser/global/index.html';

const NodesPage = createReactClass({
  displayName: 'NodesPage',
  mixins: [Reflux.connect(CurrentUserStore), Reflux.connect(NodesStore)],

  _isLoading() {
    const { nodes } = this.state;
    return !(nodes);
  },

  _renderGlobalAPIButton() {
    if (this._isLoading()) {
      return <Spinner />;
    }
    if (this._hasExternalURI()) {
      return (
        <ExternalLinkButton bsStyle="info" href={URLUtils.qualifyUrl(GLOBAL_API_BROWSER_URL)}>
          Cluster Global API browser
        </ExternalLinkButton>
      );
    }
    return null;
  },

  _hasExternalURI() {
    const { nodes } = this.state;
    const nodeVals = Object.values(nodes);
    const publishURI = URLUtils.qualifyUrl('/');
    return (nodeVals.findIndex((node) => new URI(node.transport_address).normalizePathname().toString() !== publishURI) >= 0);
  },

  render() {
    const { nodes, currentUser } = this.state;
    return (
      <DocumentTitle title="Nodes">
        <div>
          <PageHeader title="Nodes">
            <span>This page provides a real-time overview of the nodes in your Graylog cluster.</span>

            <span>
              You can pause message processing at any time. The process buffers will not accept any new messages until
              you resume it. If the message journal is enabled for a node, which it is by default, incoming messages
              will be persisted to disk, even when processing is disabled.
            </span>
            <span>
              {this._renderGlobalAPIButton()}
            </span>
          </PageHeader>
          <NodesList permissions={currentUser.permissions} nodes={nodes} />
        </div>
      </DocumentTitle>
    );
  },
});

export default NodesPage;

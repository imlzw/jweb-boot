/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.components.nameservice;

import java.io.Serializable;

/**
 * jweb服务发现配置类
 */
public class JwebDiscoveryConfig implements Serializable {

    private boolean enable = false;
    private String serviceName;
    private String namespace;
    private String groupName;
    private String registerAddress;
    private String clusterName;
    private boolean ephemeral = true;
    private String ignoredInterfaces;
    private String preferredNetworks;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isEphemeral() {
        return ephemeral;
    }

    public void setEphemeral(boolean ephemeral) {
        this.ephemeral = ephemeral;
    }

    public String getIgnoredInterfaces() {
        return ignoredInterfaces;
    }

    public void setIgnoredInterfaces(String ignoredInterfaces) {
        this.ignoredInterfaces = ignoredInterfaces;
    }

    public String getPreferredNetworks() {
        return preferredNetworks;
    }

    public void setPreferredNetworks(String preferredNetworks) {
        this.preferredNetworks = preferredNetworks;
    }
}

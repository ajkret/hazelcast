package com.dersommer.sample.hazelcast.config;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;

@Configuration
@Primary
public class HazelcastConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastConfiguration.class);

    @Value("${hazelcast.config.group.name:}")
    private String group;

    @Value("${hazelcast.config.group.password:}")
    private String password;

    @Value("${hazelcast.config.network.port:5705}")
    private int port;

    @Value("${hazelcast.config.network.port-auto-increment:true}")
    private boolean portAutoIncrement;

    @Value("${hazelcast.config.network.multicast.enabled:false}")
    private boolean multicastEnabled;

    @Value("${hazelcast.config.network.multicast.interfaces:false}")
    private String[] multicastInterfaces;

    @Value("${hazelcast.config.network.multicast.port:0}")
    private int multicastPort;

    @Value("${hazelcast.config.network.multicast.group:group}")
    private String multicastGroup;

    @Value("${hazelcast.config.network.multicast.loopback-mode:false}")
    private boolean multicastLoopbackMode;

    @Value("${hazelcast.config.network.tcp-ip.enabled:false}")
    private boolean tcpIpEnabled;

    // Comma separated
    @Value("${hazelcast.config.network.tcp-ip.members:127.0.0.1}")
    private String[] tcpMembers;

    @Value("${hazelcast.config.network.aws.enabled:false}")
    private boolean awsEnabled;

    @Value("${hazelcast.config.network.aws.access-key:}")
    private String awsAccessKey;

    @Value("${hazelcast.config.network.aws.secret-key:}")
    private String awsSecretKey;

    @Value("${hazelcast.config.network.aws.region:}")
    private String awsRegion;

    @Value("${hazelcast.config.network.aws.host-header:}")
    private String awsHostHeader;

    @Value("${hazelcast.config.network.aws.security-group-name:}")
    private String awsSecurityGroupName;

    @Value("${hazelcast.config.network.aws.tag-key:}")
    private String awsTagKey;

    @Value("${hazelcast.config.network.aws.tag-value:}")
    private String awsTagValue;

    @Value("${hazelcast.config.network.aws.i-am-role:}")
    private String iAmRole;

    @Bean
    public Config configureHazelcast() {

        Config config = new Config();

        if (!StringUtils.isEmpty(group)) {
            LOGGER.info("Hazelcast: group configuration ENABLED {} {}", group, password);
            GroupConfig groupConfig = new GroupConfig(group);
            groupConfig.setPassword(password);
            config.setGroupConfig(groupConfig);
        }

        NetworkConfig networkConfig = new NetworkConfig();

        networkConfig.setPort(port);
        networkConfig.setPortAutoIncrement(portAutoIncrement);

        LOGGER.info("Hazelcast: Port configured {} auto increment {}", port, portAutoIncrement);

        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig()
                  .setEnabled(false);
        joinConfig.getTcpIpConfig()
                  .setEnabled(false);
        joinConfig.getAwsConfig()
                  .setEnabled(false);
        if (tcpIpEnabled) {
            joinConfig.getTcpIpConfig()
                      .setEnabled(true);
            LOGGER.info("Hazelcast: TCP IP joiner {}", Arrays.asList(tcpMembers)
                                                             .stream()
                                                             .collect(Collectors.joining(",")));
            TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
            if (tcpMembers != null)
                Stream.of(tcpMembers)
                      .forEach(member -> tcpIpConfig.addMember(member));
        } else if (multicastEnabled) {
            joinConfig.getMulticastConfig()
                      .setEnabled(true);
            LOGGER.info("Hazelcast: Multicast joiner {}:{} loopback mode={} interfaces {} ", multicastGroup, multicastPort, multicastLoopbackMode,
                multicastInterfaces);
            MulticastConfig multicastConfig = joinConfig.getMulticastConfig();
            multicastConfig.setLoopbackModeEnabled(multicastLoopbackMode);
            if (multicastGroup != null)
                multicastConfig.setMulticastGroup(multicastGroup);
            if (multicastPort > 0)
                multicastConfig.setMulticastPort(multicastPort);
            if (multicastInterfaces != null)
                Stream.of(multicastInterfaces)
                      .forEach(ip -> multicastConfig.addTrustedInterface(ip));
        } else if (awsEnabled) {
            joinConfig.getAwsConfig()
                      .setEnabled(true);
            LOGGER.info("Hazelcast: AWS discovery enabled AWS Key {} region {}", awsAccessKey, awsRegion);
            AwsConfig awsConfig = joinConfig.getAwsConfig();
            awsConfig.setRegion(awsRegion);
            if (!StringUtils.isEmpty(iAmRole)) {
                awsConfig.setIamRole(iAmRole);
            } else {
                awsConfig.setAccessKey(awsAccessKey);
                awsConfig.setSecretKey(awsSecretKey);
                if (!StringUtils.isEmpty(awsSecurityGroupName)) {
                    LOGGER.info("Hazelcast: AWS Securiy Group {}", awsSecurityGroupName);
                    awsConfig.setSecurityGroupName(awsSecurityGroupName);
                }
            }
            if (!StringUtils.isEmpty(awsHostHeader)) {
                LOGGER.info("Hazelcast: AWS Host Header {}", awsHostHeader);
                awsConfig.setHostHeader(awsHostHeader);
            }
            if (!StringUtils.isEmpty(awsTagKey)) {
                LOGGER.info("Hazelcast: AWS Tag key / value {}", awsTagKey, awsTagValue);
                awsConfig.setSecurityGroupName(awsTagKey);
                awsConfig.setSecurityGroupName(awsTagValue);
            }
        }
        config.setNetworkConfig(networkConfig);

        return config;
    }

}

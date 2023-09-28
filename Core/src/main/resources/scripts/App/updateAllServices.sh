#!/bin/bash
NexiV1IP=10.20.6.20
ssh root@$NexiV1IP "systemctl stop nexiDefi.service"
\cp  /root/.m2/repository/com/plgchain/app/Cloud/0.0.1/Cloud-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/BlockchainHealthBusiness/0.0.1/BlockchainHealthBusiness-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/Transfer/0.0.1/Transfer-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/Ucenter-api/0.0.1/Ucenter-api-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/DefiMarketMaking/0.0.1/DefiMarketMaking-0.0.1.jar /root/PlingaHelperApp/
/usr/bin/systemctl stop kafka.service
/usr/bin/systemctl stop zookeeper.service
rm -rf /var/log/plingaHelper/*
rm -rf /home/kafka/data/*
rm -rf /home/zookeeper/data/*
ssh root@$NexiV1IP "rm -rf /var/log/plingaHelper/*"
scp -r /root/.m2/repository/com/plgchain/app/NexiDefi/0.0.1/NexiDefi-0.0.1.jar root@$NexiV1IP:/root/PlingaHelperApp/
/usr/bin/systemctl restart zookeeper.service
/usr/bin/systemctl restart kafka.service
/usr/bin/systemctl restart cloud.service
/usr/bin/systemctl restart blockchainHealthBusiness.service
/usr/bin/systemctl restart transfer.service
/usr/bin/systemctl restart defiMarketMaking.service
/usr/bin/systemctl restart ucenterApi.service
ssh root@$NexiV1IP "systemctl restart nexiDefi.service"
#/usr/bin/systemctl restart webapp.service
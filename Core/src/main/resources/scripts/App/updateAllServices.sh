#!/bin/bash
\cp  /root/.m2/repository/com/plgchain/app/Cloud/0.0.1/Cloud-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/BlockchainHealthBusiness/0.0.1/BlockchainHealthBusiness-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/Transfer/0.0.1/Transfer-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/Ucenter-api/0.0.1/Ucenter-api-0.0.1.jar /root/PlingaHelperApp/
\cp  /root/.m2/repository/com/plgchain/app/DefiMarketMaking/0.0.1/DefiMarketMaking-0.0.1.jar /root/PlingaHelperApp/
rm -rf /var/log/plingaHelper/*
/usr/bin/systemctl restart cloud.service
/usr/bin/systemctl restart blockchainHealthBusiness.service
/usr/bin/systemctl restart transfer.service
/usr/bin/systemctl restart defiMarketMaking.service
/usr/bin/systemctl restart ucenterApi.service
#/usr/bin/systemctl restart webapp.service

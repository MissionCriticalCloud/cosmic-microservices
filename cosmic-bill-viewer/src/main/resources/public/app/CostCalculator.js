'use strict';

const CostCalculator = Class({

    cpuPrice: undefined,
    memoryPrice: undefined,
    storagePrice: undefined,
    publicIpPrice: undefined,
    serviceFee: undefined,
    innovationFee: undefined,

    initialize: function(
        cpuPrice,
        memoryPrice,
        storagePrice,
        publicIpPrice,
        serviceFee,
        innovationFee
    ) {
        _.bindAll(this, ... _.functions(this));

        this.cpuPrice = cpuPrice;
        this.memoryPrice = memoryPrice;
        this.storagePrice = storagePrice;
        this.publicIpPrice = publicIpPrice;
        this.serviceFee = serviceFee;
        this.innovationFee = innovationFee;
    },

    calculateDomainCosts: function(domains, detailed) {
        if (detailed) {
            _.each(domains, this.calculateDetailedDomainCosts);
        } else {
            _.each(domains, this.calculateGeneralDomainCosts);
        }
    },

    calculateGeneralDomainCosts: function(domain) {
        const cpuPrice = numeral(this.cpuPrice);
        const memoryPrice = numeral(this.memoryPrice);
        const storagePrice = numeral(this.storagePrice);
        const publicIpPrice = numeral(this.publicIpPrice);

        const feesPercentage = this.getTotalFeePercentage();

        const cpuAmount = numeral(domain.usage.compute.total
            .map(function (x) {
                return numeral(x.cpu).multiply(x.duration);
            })
            .reduce(function (x, y) {
                return x.add(y.value());
            }, numeral()));

        const memoryAmount = numeral(domain.usage.compute.total
            .map(function (x) {
                return numeral(x.memory).multiply(x.duration);
            })
            .reduce(function (x, y) {
                return x.add(y.value());
            }, numeral()));

        const storageAmount = numeral(domain.usage.storage.total
            .map(function (x) {
                return numeral(x.size).multiply(x.duration);
            })
            .reduce(function (x, y) {
                return x.add(y.value());
            }, numeral()));

        domain.costs = {
            compute: {
                cpu: numeral(cpuPrice.value())
                    .multiply(cpuAmount.value()),
                memory: numeral(memoryPrice.value())
                    .multiply(memoryAmount.value())
            },
            storage: numeral(storagePrice.value())
                .multiply(storageAmount.value()),
            networking: {
                publicIps: numeral(publicIpPrice.value())
                    .multiply(domain.usage.networking.total.publicIps)
            }
        };

        domain.costs.total = numeral(domain.costs.compute.cpu.value())
            .add(domain.costs.compute.memory.value())
            .add(domain.costs.storage.value())
            .add(domain.costs.networking.publicIps.value());

        domain.costs.totalInclFees = numeral(domain.costs.total).multiply(feesPercentage.value());

        domain.usage.compute.total.cpu = numeral(cpuAmount.value()).format();
        domain.usage.compute.total.memory = numeral(memoryAmount.value()).format();
        domain.usage.storage.total = numeral(storageAmount.value()).format();
        domain.usage.networking.total.publicIps = numeral(domain.usage.networking.total.publicIps).format();

        domain.costs.compute.cpu = domain.costs.compute.cpu.format();
        domain.costs.compute.memory = domain.costs.compute.memory.format();
        domain.costs.storage = domain.costs.storage.format();
        domain.costs.networking.publicIps = domain.costs.networking.publicIps.format();

        domain.costs.total = domain.costs.total.format();
        domain.costs.totalInclFees = domain.costs.totalInclFees.format();
    },

    calculateDetailedDomainCosts: function(domain) {
        this.calculateGeneralDomainCosts(domain);

        _.each(domain.usage.storage.volumes, this.calculateVolumeCosts);
        this.attachVolumesToVirtualMachines(domain);
        _.each(domain.usage.networking.networks, this.calculateNetworkCosts);
        _.each(domain.usage.compute.virtualMachines, this.calculateVirtualMachineCosts);
    },

    calculateVirtualMachineCosts: function(virtualMachine) {
        const cpuPrice = numeral(this.cpuPrice);
        const memoryPrice = numeral(this.memoryPrice);

        const price = numeral(0);

        price.add(
            numeral(cpuPrice.value())
            .multiply(virtualMachine.cpu).value()
        );

        price.add(
            numeral(memoryPrice.value())
            .multiply(virtualMachine.memory).value()
        );

        const totalPrice = numeral(price.value());

        _.each(virtualMachine.volumes, function(volume) {
            totalPrice.add(
                numeral(volume.pricing.price).value()
            );
        });

        const priceInclFees = numeral(price.value())
                              .multiply(this.getTotalFeePercentage().value());
        const totalPriceInclFees = numeral(totalPrice.value())
                              .multiply(this.getTotalFeePercentage().value());

        virtualMachine.cpu = numeral(virtualMachine.cpu).format();
        virtualMachine.memory = numeral(virtualMachine.memory).format();
        virtualMachine.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format(),
            totalPrice: totalPrice.format(),
            totalPriceInclFees: totalPriceInclFees.format()
        };
    },

    calculateVolumeCosts: function(volume) {
        const storagePrice = numeral(this.storagePrice);

        const price = numeral(volume.size)
                      .multiply(storagePrice.value());
        const priceInclFees = numeral(price.value())
                      .multiply(this.getTotalFeePercentage().value());

        volume.size = numeral(volume.size).format();
        volume.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    calculateNetworkCosts: function(network) {
        const price = numeral(0);

        _.each(network.publicIps, this.calculatePublicIpAddressesCosts);

        _.each(network.publicIps, function(publicIp) {
            price.add(publicIp.pricing.price);
        });

        const priceInclFees = numeral(price.value())
                              .multiply(this.getTotalFeePercentage().value());

        network.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    calculatePublicIpAddressesCosts: function(publicIp) {
        const publicIpPrice = numeral(this.publicIpPrice);

        const price = numeral(publicIp.amount)
                      .multiply(publicIpPrice.value());
        const priceInclFees = numeral(price.value())
                      .multiply(this.getTotalFeePercentage().value());

        publicIp.amount = numeral(publicIp.amount).format();
        publicIp.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    getTotalFeePercentage: function() {
        const serviceFee = numeral(this.serviceFee)
                           .divide(100);
        const innovationFee = numeral(this.innovationFee)
                              .divide(100);

        return numeral(serviceFee.value())
               .add(innovationFee.value())
               .add(1);
    },

    attachVolumesToVirtualMachines: function (domain) {
        const unattachedVolumes = [];

        _.each(domain.usage.storage.volumes, function (volume) {
            if (volume.attachedTo) {
                const vm = _.find(domain.usage.compute.virtualMachines, function(vm) {
                    return vm.uuid === volume.attachedTo;
                });

                // Fix until storage metrics are cleaned.
                if (!vm) {
                    return;
                }

                if (!vm.volumes) {
                    vm.volumes = [ volume ];
                } else {
                    vm.volumes.push(volume);
                }
            } else {
                unattachedVolumes.push(volume);
            }
        });
        domain.usage.storage.volumes = unattachedVolumes;
    }
});

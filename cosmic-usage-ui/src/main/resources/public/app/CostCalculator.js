'use strict';

const CostCalculator = Class({

    cpuPrice: undefined,
    memoryPrice: undefined,
    storagePrice: undefined,
    publicIpPrice: undefined,
    serviceFeePercentage: undefined,
    innovationFeePercentage: undefined,

    initialize: function(
        cpuPrice,
        memoryPrice,
        storagePrice,
        publicIpPrice,
        serviceFeePercentage,
        innovationFeePercentage
    ) {
        _.bindAll(this, ... _.functions(this));

        this.cpuPrice = cpuPrice;
        this.memoryPrice = memoryPrice;
        this.storagePrice = storagePrice;
        this.publicIpPrice = publicIpPrice;
        this.serviceFeePercentage = serviceFeePercentage;
        this.innovationFeePercentage = innovationFeePercentage;
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

        const feesPercentage = this.getFeePercentage();

        domain.costs = {
            compute: {
                cpu: numeral(cpuPrice.value())
                     .multiply(domain.usage.compute.total.cpu),
                memory: numeral(memoryPrice.value())
                        .multiply(domain.usage.compute.total.memory)
            },
            storage: numeral(storagePrice.value())
                     .multiply(domain.usage.storage.total),
            networking: {
                publicIps: numeral(publicIpPrice.value())
                           .multiply(domain.usage.networking.total.publicIps)
            }
        };

        domain.costs.total = numeral(domain.costs.compute.cpu.value())
                             .add(domain.costs.compute.memory.value())
                             .add(domain.costs.storage.value())
                             .add(domain.costs.networking.publicIps.value());

        domain.costs.totalInclFees = numeral(domain.costs.total)
                                     .multiply(feesPercentage.value());

        domain.usage.compute.total.cpu = numeral(domain.usage.compute.total.cpu).format();
        domain.usage.compute.total.memory = numeral(domain.usage.compute.total.memory).format();
        domain.usage.storage.total = numeral(domain.usage.storage.total).format();
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
                              .multiply(this.getFeePercentage().value());
        const totalPriceInclFees = numeral(totalPrice.value())
                              .multiply(this.getFeePercentage().value());

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
                      .multiply(this.getFeePercentage().value());

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
                              .multiply(this.getFeePercentage().value());

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
                      .multiply(this.getFeePercentage().value());

        publicIp.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    getFeePercentage: function() {
        const serviceFeePercentage = numeral(this.serviceFeePercentage)
                                     .divide(100);
        const innovationFeePercentage = numeral(this.innovationFeePercentage)
                                     .divide(100);

        return numeral(serviceFeePercentage.value())
               .add(innovationFeePercentage.value())
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

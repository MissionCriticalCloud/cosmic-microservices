'use strict';

const CostCalculator = Class({

    cpuPrice: undefined,
    memoryPrice: undefined,
    storagePrice: undefined,
    publicIpPrice: undefined,
    serviceFee: undefined,
    innovationFee: undefined,

    convertToGB: 1024 * 1024 * 1024,
    convertToHours: 60 * 60,

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
    calculateDomainCosts: function (domain) {
        this.calculateStorageCosts(domain.usage.storage);
        this.attachVolumesToVirtualMachines(domain);
        this.calculateNetworkingCosts(domain.usage.networking);
        this.calculateComputeCosts(domain.usage.compute);

        this.calculateTotalDomainPrices(domain);
    },

    calculateStorageCosts: function (storage) {
        _.each(storage.volumes, this.calculateVolumeCosts);
    },

    calculateComputeCosts: function(compute) {
        _.each(compute.virtualMachines, this.calculateVirtualMachineCosts);
    },

    calculateNetworkingCosts: function(networking) {
        _.each(networking.networks, this.calculateNetworkCosts);
    },

    addTotalNetworkCosts: function (networking) {
        const price = numeral(0);
        const priceInclFees = numeral(0);
        _.each(networking.networks, function (network) {
            price.add(numeral((network.pricing.price)).value());
            priceInclFees.add(numeral((network.pricing.priceInclFees)).value());
        });

        networking.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },
    addTotalStorageCosts: function (storage) {
        const price = numeral(0);
        const priceInclFees = numeral(0);
        _.each(storage.volumes, function (volume) {
            price.add(numeral((volume.pricing.price)).value());
            priceInclFees.add(numeral((volume.pricing.priceInclFees)).value());
        });

        storage.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },
    addTotalVMCosts: function (compute) {
        const price = numeral(0);
        const priceInclFees = numeral(0);
        _.each(compute.virtualMachines, function (virtualMachine) {
            price.add(numeral((virtualMachine.pricing.price)).value());
            priceInclFees.add(numeral((virtualMachine.pricing.priceInclFees)).value());
        });

        compute.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },
    calculateTotalDomainPrices: function (domain) {
        const vmPrice = numeral(0);
        const vmPriceInclFee = numeral(0);
        const storagePrice = numeral(0);
        const storagePriceInclFee = numeral(0);
        const networkPrice = numeral(0);
        const networkPriceInclFee = numeral(0);

        this.addTotalVMCosts(domain.usage.compute);
        this.addTotalStorageCosts(domain.usage.storage);
        this.addTotalNetworkCosts(domain.usage.networking);

        _.each(domain.usage.storage.volumes, function (volume) {
            var tmpVal;
            tmpVal = (numeral(volume.pricing.price)).value();
            storagePrice.add(tmpVal);
            var tmpFeeVal;
            tmpFeeVal = (numeral(volume.pricing.priceInclFees)).value();
            storagePriceInclFee.add(tmpFeeVal);
        });

        _.each(domain.usage.compute.virtualMachines, function (virtualMachine) {
            var tmpVal;
            tmpVal = (numeral(virtualMachine.pricing.price)).value();
            vmPrice.add(tmpVal);
            var tmpFeeVal;
            tmpFeeVal = (numeral(virtualMachine.pricing.priceInclFees)).value();
            vmPriceInclFee.add(tmpFeeVal);
        });
        _.each(domain.usage.networking.networks, function (network) {
            var tmpVal;
            tmpVal = (numeral(network.pricing.price)).value();
            networkPrice.add(tmpVal);
            var tmpFeeVal;
            tmpFeeVal = (numeral(network.pricing.priceInclFees)).value();
            networkPriceInclFee.add(tmpFeeVal);
        });

        var total = numeral(storagePrice.value() + networkPrice.value() + vmPrice.value());
        var totalInclFee = numeral(storagePriceInclFee.value() + networkPriceInclFee.value() + vmPriceInclFee.value());
        domain.pricing = {
            vmPrice: vmPrice.format(),
            storagePrice: storagePrice.format(),
            networkPrice: networkPrice.format(),
            total: total.format(),
            totalInclFee: totalInclFee.format()
        };
    },
    calculateVirtualMachineCosts: function (virtualMachine) {
        const price = numeral(0);
        _.each(virtualMachine.instanceTypes, this.calculateInsanceTypeCosts);
        _.each(virtualMachine.instanceTypes, function (instanceType) {
            var tmpVal;
            tmpVal = (numeral(instanceType.pricing.price)).value();
            price.add(tmpVal);
        });
        _.each(virtualMachine.volumes, function (volume) {
            var tmpVal;
            tmpVal = (numeral(volume.pricing.price)).value();
            price.add(tmpVal);
        });
        const priceInclFees = numeral(price.value())
            .multiply(this.getTotalFeePercentage().value());

        virtualMachine.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };

    },
    calculateInsanceTypeCosts: function (instanceType) {
        const cpuPrice = numeral(this.cpuPrice);
        const memoryPrice = numeral(this.memoryPrice);

        const price = numeral(0);

        price.add(
            numeral(cpuPrice.value())
                .multiply(instanceType.cpu)
                .multiply(instanceType.duration)
                .divide(this.convertToHours).value()
        );

        price.add(
            numeral(memoryPrice.value())
                .multiply(instanceType.memory)
                .multiply(instanceType.duration)
                .divide(this.convertToGB)
                .divide(this.convertToHours).value()
        );

        const totalPrice = numeral(price.value());

        _.each(instanceType.volumes, function (volume) {
            totalPrice.add(
                numeral(volume.pricing.price).value()
            );
        });

        const priceInclFees = numeral(price.value())
            .multiply(this.getTotalFeePercentage().value());
        const totalPriceInclFees = numeral(totalPrice.value())
            .multiply(this.getTotalFeePercentage().value());

        instanceType.cpu = numeral(instanceType.cpu).format();
        instanceType.memory = numeral(instanceType.memory).format();
        instanceType.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format(),
            totalPrice: totalPrice.format(),
            totalPriceInclFees: totalPriceInclFees.format()
        };
    },

    calculateVolumeCosts: function (volumes) {
        const price = numeral(0);
        _.each(volumes.volumeSizes, this.calculateVolumeSizeCost);
        _.each(volumes.volumeSizes, function (volumeSize) {
            var tmpVal;
            tmpVal = (numeral(volumeSize.pricing.price)).value();
            price.add(tmpVal);
        });

        const priceInclFees = numeral(price.value())
            .multiply(this.getTotalFeePercentage().value());

        volumes.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    calculateVolumeSizeCost: function (volume) {
        const storagePrice = numeral(this.storagePrice);
        const toGB = numeral(this.convertToGB);
        const price = numeral(volume.size)
            .multiply(storagePrice.value())
            .multiply(volume.duration)
            .divide(this.convertToHours)
            .divide(toGB.value());
        const priceInclFees = numeral(price.value())
            .multiply(this.getTotalFeePercentage().value());

        volume.size = numeral(volume.size).format();
        volume.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    calculateNetworkCosts: function (network) {
        const price = numeral(0);

        _.each(network.publicIps, this.calculatePublicIpAddressesCosts);

        _.each(network.publicIps, function (publicIp) {
            price.add(publicIp.pricing.price);
        });

        const priceInclFees = numeral(price.value())
            .multiply(this.getTotalFeePercentage().value());

        network.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    calculatePublicIpAddressesCosts: function (publicIp) {
        const publicIpPrice = numeral(this.publicIpPrice);

        const toHours = numeral(this.convertToHours);
        const price = numeral(publicIp.duration)
            .multiply(publicIpPrice.value())
            .divide(toHours.value());
        const priceInclFees = numeral(price.value())
            .multiply(this.getTotalFeePercentage().value());

        publicIp.duration = numeral(publicIp.duration).format();
        publicIp.pricing = {
            price: price.format(),
            priceInclFees: priceInclFees.format()
        };
    },

    getTotalFeePercentage: function () {
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
                const vm = _.find(domain.usage.compute.virtualMachines, function (vm) {
                    return vm.uuid === volume.attachedTo;
                });

                // Fix until storage metrics are cleaned.
                if (!vm) {
                    return;
                }

                if (!vm.volumes) {
                    vm.volumes = [volume];
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

'use strict';

const Class = function(methods) {
    const klass = function() {
        this.initialize.apply(this, arguments);
    };

    for (const property in methods) {
        klass.prototype[property] = methods[property];
    }

    if (!klass.prototype.initialize) {
        klass.prototype.initialize = function() {
            // Empty
        };
    }

    return klass;
};

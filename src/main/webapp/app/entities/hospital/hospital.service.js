(function() {
    'use strict';
    angular
        .module('monMedecinApp')
        .factory('Hospital', Hospital);

    Hospital.$inject = ['$resource'];

    function Hospital ($resource) {
        var resourceUrl =  'api/hospitals/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

(function() {
    'use strict';
    angular
        .module('monMedecinApp')
        .factory('Town', Town);

    Town.$inject = ['$resource'];

    function Town ($resource) {
        var resourceUrl =  'api/towns/:id';

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

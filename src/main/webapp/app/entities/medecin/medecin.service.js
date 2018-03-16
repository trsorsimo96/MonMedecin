(function() {
    'use strict';
    angular
        .module('monMedecinApp')
        .factory('Medecin', Medecin);

    Medecin.$inject = ['$resource'];

    function Medecin ($resource) {
        var resourceUrl =  'api/medecins/:id';

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

(function() {
    'use strict';
    angular
        .module('monMedecinApp')
        .factory('Arrondissement', Arrondissement);

    Arrondissement.$inject = ['$resource'];

    function Arrondissement ($resource) {
        var resourceUrl =  'api/arrondissements/:id';

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

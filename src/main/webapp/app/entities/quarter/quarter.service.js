(function() {
    'use strict';
    angular
        .module('monMedecinApp')
        .factory('Quarter', Quarter);

    Quarter.$inject = ['$resource'];

    function Quarter ($resource) {
        var resourceUrl =  'api/quarters/:id';

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

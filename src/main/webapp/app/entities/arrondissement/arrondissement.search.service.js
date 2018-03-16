(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .factory('ArrondissementSearch', ArrondissementSearch);

    ArrondissementSearch.$inject = ['$resource'];

    function ArrondissementSearch($resource) {
        var resourceUrl =  'api/_search/arrondissements/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

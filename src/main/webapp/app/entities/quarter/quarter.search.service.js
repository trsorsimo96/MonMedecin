(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .factory('QuarterSearch', QuarterSearch);

    QuarterSearch.$inject = ['$resource'];

    function QuarterSearch($resource) {
        var resourceUrl =  'api/_search/quarters/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

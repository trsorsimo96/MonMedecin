(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .factory('SubCategorySearch', SubCategorySearch);

    SubCategorySearch.$inject = ['$resource'];

    function SubCategorySearch($resource) {
        var resourceUrl =  'api/_search/sub-categories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();

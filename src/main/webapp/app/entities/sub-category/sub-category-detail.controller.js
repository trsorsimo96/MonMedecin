(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('SubCategoryDetailController', SubCategoryDetailController);

    SubCategoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'SubCategory', 'Category'];

    function SubCategoryDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, SubCategory, Category) {
        var vm = this;

        vm.subCategory = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('monMedecinApp:subCategoryUpdate', function(event, result) {
            vm.subCategory = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

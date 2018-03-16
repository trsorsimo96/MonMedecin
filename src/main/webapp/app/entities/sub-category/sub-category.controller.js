(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('SubCategoryController', SubCategoryController);

    SubCategoryController.$inject = ['DataUtils', 'SubCategory', 'SubCategorySearch'];

    function SubCategoryController(DataUtils, SubCategory, SubCategorySearch) {

        var vm = this;

        vm.subCategories = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            SubCategory.query(function(result) {
                vm.subCategories = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            SubCategorySearch.query({query: vm.searchQuery}, function(result) {
                vm.subCategories = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();

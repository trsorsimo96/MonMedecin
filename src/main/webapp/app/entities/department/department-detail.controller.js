(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('DepartmentDetailController', DepartmentDetailController);

    DepartmentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Department', 'Region'];

    function DepartmentDetailController($scope, $rootScope, $stateParams, previousState, entity, Department, Region) {
        var vm = this;

        vm.department = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('monMedecinApp:departmentUpdate', function(event, result) {
            vm.department = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

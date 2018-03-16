(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .controller('QuarterDetailController', QuarterDetailController);

    QuarterDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Quarter', 'Town'];

    function QuarterDetailController($scope, $rootScope, $stateParams, previousState, entity, Quarter, Town) {
        var vm = this;

        vm.quarter = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('monMedecinApp:quarterUpdate', function(event, result) {
            vm.quarter = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

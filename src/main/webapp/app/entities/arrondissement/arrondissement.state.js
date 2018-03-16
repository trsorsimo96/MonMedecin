(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('arrondissement', {
            parent: 'entity',
            url: '/arrondissement',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.arrondissement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/arrondissement/arrondissements.html',
                    controller: 'ArrondissementController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('arrondissement');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('arrondissement-detail', {
            parent: 'arrondissement',
            url: '/arrondissement/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.arrondissement.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/arrondissement/arrondissement-detail.html',
                    controller: 'ArrondissementDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('arrondissement');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Arrondissement', function($stateParams, Arrondissement) {
                    return Arrondissement.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'arrondissement',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('arrondissement-detail.edit', {
            parent: 'arrondissement-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/arrondissement/arrondissement-dialog.html',
                    controller: 'ArrondissementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Arrondissement', function(Arrondissement) {
                            return Arrondissement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('arrondissement.new', {
            parent: 'arrondissement',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/arrondissement/arrondissement-dialog.html',
                    controller: 'ArrondissementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('arrondissement', null, { reload: 'arrondissement' });
                }, function() {
                    $state.go('arrondissement');
                });
            }]
        })
        .state('arrondissement.edit', {
            parent: 'arrondissement',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/arrondissement/arrondissement-dialog.html',
                    controller: 'ArrondissementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Arrondissement', function(Arrondissement) {
                            return Arrondissement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('arrondissement', null, { reload: 'arrondissement' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('arrondissement.delete', {
            parent: 'arrondissement',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/arrondissement/arrondissement-delete-dialog.html',
                    controller: 'ArrondissementDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Arrondissement', function(Arrondissement) {
                            return Arrondissement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('arrondissement', null, { reload: 'arrondissement' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

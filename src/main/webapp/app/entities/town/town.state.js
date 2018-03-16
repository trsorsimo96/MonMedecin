(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('town', {
            parent: 'entity',
            url: '/town',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.town.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/town/towns.html',
                    controller: 'TownController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('town');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('town-detail', {
            parent: 'town',
            url: '/town/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.town.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/town/town-detail.html',
                    controller: 'TownDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('town');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Town', function($stateParams, Town) {
                    return Town.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'town',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('town-detail.edit', {
            parent: 'town-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/town/town-dialog.html',
                    controller: 'TownDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Town', function(Town) {
                            return Town.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('town.new', {
            parent: 'town',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/town/town-dialog.html',
                    controller: 'TownDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                image: null,
                                imageContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('town', null, { reload: 'town' });
                }, function() {
                    $state.go('town');
                });
            }]
        })
        .state('town.edit', {
            parent: 'town',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/town/town-dialog.html',
                    controller: 'TownDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Town', function(Town) {
                            return Town.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('town', null, { reload: 'town' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('town.delete', {
            parent: 'town',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/town/town-delete-dialog.html',
                    controller: 'TownDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Town', function(Town) {
                            return Town.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('town', null, { reload: 'town' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

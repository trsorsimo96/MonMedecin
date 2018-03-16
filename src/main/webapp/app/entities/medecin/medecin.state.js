(function() {
    'use strict';

    angular
        .module('monMedecinApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('medecin', {
            parent: 'entity',
            url: '/medecin',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.medecin.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/medecin/medecins.html',
                    controller: 'MedecinController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('medecin');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('medecin-detail', {
            parent: 'medecin',
            url: '/medecin/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'monMedecinApp.medecin.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/medecin/medecin-detail.html',
                    controller: 'MedecinDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('medecin');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Medecin', function($stateParams, Medecin) {
                    return Medecin.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'medecin',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('medecin-detail.edit', {
            parent: 'medecin-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/medecin/medecin-dialog.html',
                    controller: 'MedecinDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Medecin', function(Medecin) {
                            return Medecin.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('medecin.new', {
            parent: 'medecin',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/medecin/medecin-dialog.html',
                    controller: 'MedecinDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                title: null,
                                email: null,
                                phone: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('medecin', null, { reload: 'medecin' });
                }, function() {
                    $state.go('medecin');
                });
            }]
        })
        .state('medecin.edit', {
            parent: 'medecin',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/medecin/medecin-dialog.html',
                    controller: 'MedecinDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Medecin', function(Medecin) {
                            return Medecin.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('medecin', null, { reload: 'medecin' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('medecin.delete', {
            parent: 'medecin',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/medecin/medecin-delete-dialog.html',
                    controller: 'MedecinDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Medecin', function(Medecin) {
                            return Medecin.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('medecin', null, { reload: 'medecin' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

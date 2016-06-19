/* global malarkey:false, moment:false */

import { config } from './index.config';
import { runBlock } from './index.run';
import { MainController } from './main/main.controller';

angular.module('trello', ['ngAnimate', 'ngCookies', 'ngTouch', 'ngSanitize', 'ngMessages', 'ngAria', 'toastr', angularDragula(angular)])
  .config(config)
  .run(runBlock)
  .controller('MainController', MainController)

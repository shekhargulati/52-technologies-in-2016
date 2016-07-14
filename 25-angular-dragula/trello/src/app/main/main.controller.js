export class MainController {
  constructor($scope) {
    'ngInject';

    $scope.$on('bag.drop', function(e, el, target){
      console.log(`Dropped ${el[0].id} on target ${target[0].id}`);
    });


    this.board = {
      title: "Test Board",
      lists: [{
        id: 1,
        name: "Todo",
        cards: [{
          id: 1,
          title: "Lean Go programming language",
          description: "I want to learn Go so that I can build applications with it."
        }, {
          id: 2,
          title: "Finish Missing Kids Android application",
          description: "Work on my Android application"
        }]
      }, {
        id: 2,
        name: "In Progress",
        cards: [{
          id: 3,
          title: "Blog about Angular Dragula",
          description: "Write week 25 blog on Angular Dragula"
        }]

      }, {
        id: 3,
        name: "Done",
        cards: [{
          id: 5,
          title: "Blog about Jekyll to WordPress migration",
          description: "Write week 24 blog on migrating from Jekyll to WordPress"
        }]
      }]
    }
  }


}

## Devoir: L'expert du Sudoku

### Objectifs

* Modéliser un problème de résolution de contraintes en formules de logique propositionnelle
* Utiliser un solveur SAT pour générer des données de test respectant ces contraintes

### Contexte

Microsoft a décidé de remplacer le jeu par défaut dans son système d'exploitation Windows. Plutôt que de fournir le bon vieux Démineur, le futur Windows 30 contiendra plutôt un jeu de sudoku. On rappelle qu'un sudoku est une grille de taille 9×9 dans laquelle on doit placer exactement un chiffre entre 0 et 9 dans chacune des cases. Un problème sudoku possède généralement un certain nombre de cases pré-remplies, comme ci-dessous:

![Grille de sudoku](https://github.com/ethicnology/uqac-958-sat-solver/blob/main/sudoku.png)

Le joueur cherche à compléter la grille selon les conditions suivantes:

* Chaque case ne peut contenir qu’un seul chiffre
* Chaque chiffre doit apparaître exactement une fois dans chaque ligne de la grille
* Chaque chiffre doit apparaître exactement une fois dans chaque colonne de la grille
* Chaque chiffre doit apparaître exactement une fois dans chacune des neuf sous-grilles de taille 3×3

Vous êtes en charge de tester ce logiciel. Plus précisément, vous devez vous assurer que le jeu ne propose que des grilles pour lesquelles il existe une solution; de plus, un de vos tests doit visiter la partie du code de l'application qui annonce que le joueur a gagné. Mais voilà: comme le jeu ne propose jamais la même grille à chaque exécution, il vous est impossible de simplement coder en dur les données de votre test.

Plutôt que de programmer vous-même un algorithme pour résoudre un sudoku, vous décidez d'encoder le puzzle sous forme d'un problème de satisfaisabilité booléenne.

### Préalables techniques

Le dépôt stev-booleans fournit une librairie Java permettant de créer et de manipuler des formules de logique propostionnelle. Un programme d'exemple montre les opérations à effectuer pour créer des formules booléennes, en afficher le contenu, les convertir en forme normale conjonctive, et exporter les clauses sous la forme d'un tableau d'entiers.

De son côté, la librairie Sat4j fournit un solveur SAT écrit en Java. Parmi toutes les archives disponibles, les fichiers JAR dont vous aurez besoin sont contenus dans cette archive. La page Howto du site de Sat4j donne un exemple de code permettant de créer une instance de solveur et de l'appeler en lui donnant un problème à résoudre.

Comme la plupart des solveurs SAT, celui de Sat4j demande qu'on lui passe une formule en CNF. La manière de représenter cette formule s'appelle le format DIMACS. Dans ce format, chaque variable est associée à un nombre entier; chaque clause est une liste de nombres correspondant aux variables de la clause; le nombre est négatif si la variable apparaît avec une négation, et positif sinon.

Par exemple, la formule suivante en CNF…

```
(a ∨ ¬b ∨ c) ∧ (b ∨ ¬c ∨ ¬d ∨ e)
```

…sera traduite en un tableau comme celui-ci:

```
1 -2 3
2 -3 4 5
```

Dans cet exemple, la variable a est associée au nombre 1, la variable b au nombre 2, etc.

Le "pont" entre stev-booleans et Sat4j se fait au moyen de la méthode getClauses(), qui, à partir d'un objet de type BooleanFormula mis en CNF, produit une liste de tableaux d'entiers. On passe ces tableaux successivement à la méthode addClause() d'un ISolver de la librairie Sat4j.

### Instructions

On vous demande d'écrire un programme Java qui accepte comme entrée un puzzle sudoku, et retourne en sortie une solution au puzzle. Les entrées et les sorties sont représentées sous la forme d'une chaîne de 81 caractères représentant la valeur de chaque case en lisant de gauche à droite et de haut en bas. Le symbole # représente une case vide. Ainsi, les deux premières lignes de la grille ci-dessus correspondent à la chaîne ``` #26###81#3##7#8##6``` .

L'entrée doit être donnée via une chaîne de caractères passée dans l'argument args de la méthode main() du programme. La sortie doit être imprimée dans la console. Un court tutoriel explique comment passer des arguments à main() dans Eclipse.

Identifiez d'abord les variables propositionnelles nécessaires à la modélisation ainsi que leur signification. Ensuite, modélisez chacune des propriétés 1-4 ci-dessus sous forme de contrainte de logique propositionnelle. Il n'est pas nécessaire d'utiliser la forme normale conjonctive. Finalement, traduisez le contenu de la grille de départ en conditions additionnelles faisant intervenir les variables de votre modélisation.


### Notation

Les points seront accordés au devoir selon le barème suivant:
|Élément|	Points|
|-------|---------|
|Choix des variables propositionnelles	|1|
|Modélisation de la propriété 1	|2|
|Modélisation de la propriété 2	|2|
|Modélisation de la propriété 3	|2|
|Modélisation de la propriété 4	|2|
|Modélisation du contenu de départ de la grille	|2|
|Interaction correcte avec le solveur	|1|
|Fonctionnement correct des entrées/sorties |1|
|Conversion de la solution en grille complète	|2|
|Total	|15|

Un travail qui ne respecte pas les consignes, qui ne compile pas ou qui s'exécute en produisant des erreurs inattendues se verra donner la note de zéro. Il est de la responsabilité de l'étudiant de vérifier les fichiers avant de les remettre.

# Ankinator

Ankinator sert à récupérer des définitions auprès de [Larousse](http://www.larousse.fr/dictionnaires/francais/) pour faire des paquets de carte pour le logiciel Ankiweb (http://apps.ankiweb.net/).

Il peut être utilisé pour apprendre des listes de vocabulaire, par exemple, lors de la préparation de concours (orthophonie notamment).

Une version est déployée ici : https://ankinator.herokuapp.com/

## Import dans l'application "Client lourd" Anki

* Télécharger le fichier sous format ANKI
* Ouvrir Anki et aller sur
  * `Fichier`
  * `Importer`
  * Sélectionner le fichier téléchargé sur Ankinator
  
* S'assurer que :
  * Le paquet est bien celui où vous voulez le créer (ne pas hésiter à en créer un pour catégoriser vos listes)
  * `Champ séparés par : ` a bien la valeur `Tabulation`
  * Le champ `Tolérer du HTML dans les champs` est coché
* Cliquer sur `Importer`

## Import dans l'application mobile

* Exporter sous le format `Tas de paquet ANKI (*.apkg)`
* Transférer ce fichier sur votre téléphone, et l'ouvrir
* Suivre les instructions de l'application mobile

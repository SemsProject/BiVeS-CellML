Flatten CellML Models 
======================

BiVeS is able to flatten CellML models. Flatten a model means: imports defined in a model are written into this model so that it becomes a single flat file.

In most cases this is pretty straight forward, but there are some cases that needed further attention:

* if an entity, that should be imported, uses a ```cmeta:id` equal to another entity in the model the `cmeta:id``` attribute in the entity and the corresponding RDF description is renamed, if existent. (see [renaming](#renaming-procedure))
* if an entity, that should be imported because of dependency issues of another import, uses the same ```name` as another entity in the model the entity gets renamed. (see [renaming](#renaming-procedure)) an example for this scenario is an imported component which defines a variable `V` having units `X` while there is another units definition for X in the importing document. here we need to rename `X` in the imported document to `Y` and modify the definition of `V` to use `Y```.

Renaming procedure 
-------------------

(as of version 1.3)

* if ```str` needs to be renamed we add an `_imported` to `str```
* while ```str_imported` still needs to be renamed we iterate the alphabet and concatenate it together with an `_` (`str_imported_A`, `str_imported_B`, ..., `str_imported_Z`, `str_imported_AA```..) until we found a valid name

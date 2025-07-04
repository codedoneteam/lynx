package lynx.folder

import lynx.types.Facts

trait Folder[A, B] {
  def fold(facts: Facts[A]): B
}

object Folder {
  def apply[A, B](implicit folder: Folder[A, B]): Folder[A, B] = folder
}

package org.yotchang4s.gikolet.board

import android.view._
import android.widget._
import android.content.Context

import org.yotchang4s.ch2.board._
import org.yotchang4s.gikolet.R

class BoardAdapter(context: Context) extends BaseAdapter {
  private[this] val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  private[this] var categories: List[Category] = Nil
  private[this] var categoryIndex: Option[Int] = None

  def setCategories(categories: List[Category]) {
    this.categories = if (categories != null) categories else Nil
  }

  def getCategories: List[Category] = categories

  def setCategoryIndex(categoryIndex: Option[Int]) {
    this.categoryIndex = categoryIndex
  }

  def getCategoryIndex: Option[Int] = {
    categoryIndex
  }

  def getItem(position: Int) = {
    categoryIndex match {
      case Some(i) => categories(i).getBoards(position)
      case None => categories(position)
    }
  }

  def getCount = {
    categoryIndex match {
      case Some(i) => categories(i).getBoards.size
      case None => categories.size
    }
  }

  def getItemId(position: Int) = {
    categoryIndex match {
      case Some(i) =>
        31 + position.hashCode * 31 + categories(i).hashCode * 31 + categories(i).getBoards(position).hashCode * 31
      case None =>
        31 + position.hashCode * 31 + categories(position).hashCode * 31
    }

  }

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val view =
      if (convertView == null) {
        inflater.inflate(R.layout.boards_item, null, false)
      } else {
        convertView
      }
    val textView = view.asInstanceOf[TextView]

    val label = categoryIndex match {
      case Some(i) => categories(i).getBoards(position).name
      case None => categories(position).name
    }

    textView.setText(label)

    textView
  }
}

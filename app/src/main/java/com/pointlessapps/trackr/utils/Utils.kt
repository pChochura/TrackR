package com.pointlessapps.trackr.utils

import android.content.res.Resources
import android.graphics.Point

object Utils {
	fun getScreenSize() =
		Point(
			Resources.getSystem().displayMetrics.widthPixels,
			Resources.getSystem().displayMetrics.heightPixels
		)

	open class SingletonHolder<T : Any, in A>(creator: (A?) -> T) {
		private var creator: ((A?) -> T)? = creator

		@Volatile
		protected var instance: T? = null

		fun init(arg: A? = null): T {
			return synchronized(this) {
				if (instance != null) instance!!
				else {
					val created = creator!!(arg)
					instance = created
					creator = null
					created
				}
			}
		}
	}
}
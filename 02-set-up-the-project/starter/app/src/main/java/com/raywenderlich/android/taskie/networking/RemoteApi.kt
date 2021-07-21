/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.taskie.networking

import com.raywenderlich.android.taskie.model.*
import com.raywenderlich.android.taskie.model.request.AddTaskRequest
import com.raywenderlich.android.taskie.model.request.UserDataRequest

/**
 * Holds decoupled logic for all the API calls.
 */

const val BASE_URL = "https://taskie-rw.herokuapp.com"

class RemoteApi(private val apiService: RemoteApiService) {

    suspend fun loginUser(userDataRequest: UserDataRequest): Result<String> = try {
        val data = apiService.loginUser(userDataRequest)
        Success(data.token!!)
    } catch (error: Throwable) {
        Failure(error)
    }

    suspend fun registerUser(userDataRequest: UserDataRequest): Result<String> = try {
        val data = apiService.registerUser(userDataRequest)
        Success(data.message!!)
    } catch (error: Throwable) {
        Failure(error)
    }

    suspend fun getTasks(): Result<List<Task>> = try {
        val data = apiService.getNotes()
        Success(data.notes.filter { !it.isCompleted  })
    } catch (error: Throwable) {
        Failure(error)
    }

    suspend fun deleteTask(noteId: String): Result<String> = try {
        val data = apiService.deleteNote(noteId)
        Success(data.message)
        } catch (error: Throwable) {
            Failure(error)
        }


    suspend fun completeTask(taskId: String): Result<String> = try {
       val data = apiService.completeTask(taskId)
       Success(data.message!!)
    } catch (error: Throwable) {
        Failure(error)
    }

    suspend fun addTask(addTaskRequest: AddTaskRequest): Result<Task> = try {
       val data = apiService.addTask(addTaskRequest)
       Success(data)
    } catch (error: Throwable) {
        Failure(error)
    }

    suspend fun getUserProfile(): Result<UserProfile> = try {
        val task = getTasks()

        if (task is Failure) {
            Failure(task.error)
        } else {
            val notes = task as Success
            val data = apiService.getMyProfile()

            if (data.email == null || data.name == null) {
                Failure(NullPointerException("No data!"))
            } else {
                Success(UserProfile(
                    email = data.email,
                    name = data.name,
                    numberOfNotes = task.data.size
                ))
            }
        }
    } catch (error: Throwable) {
        Failure(error)
    }
}